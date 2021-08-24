package is.toxic.GMD.service;

import io.vavr.control.Try;
import is.toxic.GMD.entity.MailEntity;
import is.toxic.GMD.repository.MailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MailSendingService {
    private final JavaMailSender emailSender;
    private final MessageService messageService;
    private final AtomicInteger sendInDay = new AtomicInteger();
    private final MailsRepository repository;

    @Value("${GMD.max-in-day}")
    private int maxInDay;

    @Value("${GMD.max-in-pack}")
    private int maxInPack;

    @Async
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Moscow")
    public void dropActualSendYetMails() {
        log.info("reset send yet num to 0");
        sendInDay.set(0);
    }

    public void sendOffers() {
        List<MimeMessage> messagesForSending = new ArrayList<>();
        List<MailEntity> entitiesForSending = repository.findTop50ByUnsubscribeAndSendYetAndSubjectNotNullAndMessageNotNullOrderByAddingData(false, false);
        log.info("Get {} tasks for distribute from db", entitiesForSending.size());
        entitiesForSending = entitiesForSending.size() <= maxInPack ? entitiesForSending : entitiesForSending.subList(0, maxInPack);
        if (entitiesForSending.size() > 0) {
            entitiesForSending.forEach(mailEntity -> {
                if (sendInDay.getAndIncrement() < maxInDay && messagesForSending.size() < maxInPack) {
                    MimeMessage forSend = messageService.getMessage(mailEntity.getEmail(), mailEntity.getMessage(), mailEntity.getSubject());
                    messagesForSending.add(forSend);
                    log.debug("Add message #{} for sending", sendInDay.get());
                } else {
                    log.debug("Can`t add message for sending, limit={}, now={}", maxInDay, sendInDay.get());
                }
            });
            Try.run(() -> emailSender.send(messagesForSending.toArray(new MimeMessage[0])))
                    .onSuccess(unused -> {
                        log.info("Success send message to server, send mails= {}", messagesForSending.size());
                        messagesForSending.forEach(mimeMessage ->
                                setEmailAsSendYet(Try.of(mimeMessage::getAllRecipients).get()[0].toString())
                        );
                    })
                    .onFailure(throwable -> log.error("Error for sending message", throwable))
                    .get();
        }
    }

    private void setEmailAsSendYet(String email) {
        MailEntity forSave = new MailEntity();
        forSave.setEmail(email);
        forSave.setSendYet(true);
        repository.save(forSave);
    }
}
