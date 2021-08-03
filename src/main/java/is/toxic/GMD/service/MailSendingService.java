package is.toxic.GMD.service;

import io.vavr.control.Try;
import is.toxic.GMD.DTO.GosbaseTradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MailSendingService {
    private final JavaMailSender emailSender;
    private final MessageService messageService;
    private final AtomicInteger sendInDay = new AtomicInteger();

    @Value("${GMD.max-in-day}")
    private int maxInDay;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Moscow")
    public void dropActualSendedMails() {
        log.info("reset sended num to 0");
        sendInDay.set(0);
    }

    public void sendMail(GosbaseTradeResponse... tradeResponses) {
        List<MimeMessage> messagesForSending = new ArrayList<>();
        List<MimeMessage> simpleMailMessages = getMessagesForSend(tradeResponses);
        if (simpleMailMessages.size() > 0) {
            simpleMailMessages.forEach(simpleMailMessage -> {
                if (sendInDay.getAndIncrement() < maxInDay && simpleMailMessage != null) {
                    messagesForSending.add(simpleMailMessage);
                    log.debug("Add message #{} for sending", sendInDay.get());
                } else {
                    log.debug("Can`t add message for sending, limit={}, now={}", maxInDay, sendInDay.get());
                    return;
                }

                Try.run(() -> emailSender.send(messagesForSending.toArray(new MimeMessage[0])))
                                .onSuccess(unused ->
                                        log.info("Success send message to server, sended mails= {}", messagesForSending.size()))
                                .onFailure(throwable -> log.error("Error for sending message", throwable))
                                .get();
                    }
            );
        }
    }

    @NonNull
    private List<MimeMessage> getMessagesForSend(GosbaseTradeResponse... tradeResponses) {
        List<MimeMessage> result = new ArrayList<>();
        AtomicInteger mailErrors = new AtomicInteger();
        AtomicInteger subErrors = new AtomicInteger();
        AtomicInteger textErrors = new AtomicInteger();
        Arrays.stream(tradeResponses)
                .parallel()
                .forEach(trade -> Try.runRunnable(() ->
                                result.add(messageService.getMimeMessage(trade, subErrors, mailErrors, textErrors)))
                        .getOrNull()
                );
        result.remove(null);
        log.info("For create prepare: {}", tradeResponses.length);
        log.info("Errors for data: text={}, mail={}, subject={}", textErrors.get(), mailErrors.get(), subErrors.get());
        log.info("Create {} messages for sending", result.size());
        return result;
    }
}
