package is.toxic.GMD.service;

import io.vavr.control.Try;
import is.toxic.GMD.DTO.GosbaseTradeResponse;
import is.toxic.GMD.util.ResourceReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MailSendingService {
    private final JavaMailSender emailSender;
    private final ResourceReader resourceReader;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(GosbaseTradeResponse... tradeResponses) {
        Try.of(() -> getMessagesForSend(tradeResponses))
                .andThen(simpleMailMessages ->
                        simpleMailMessages.forEach(simpleMailMessage ->
                                Try.run(() -> Thread.sleep(1500L))
                                        .andThen(() -> emailSender.send(simpleMailMessage))
                                        .onSuccess(unused -> log.info("Success send message to server"))
                                        .get())
                )
                .onSuccess(unused -> log.info("Success send all messages to server"))
                .onFailure(throwable -> log.error("Error for sending message", throwable))
                .get();
    }

    @NonNull
    private List<SimpleMailMessage> getMessagesForSend(GosbaseTradeResponse... tradeResponses){
        List<SimpleMailMessage> result = new ArrayList<>();
        AtomicInteger mailErrors = new AtomicInteger();
        AtomicInteger subErrors = new AtomicInteger();
        AtomicInteger textErrors = new AtomicInteger();
        Arrays.stream(tradeResponses)
                .parallel()
                .forEach(trade ->
                        Try.runRunnable(() -> {
                            String mail = Try.of(() -> getEmail(trade))
                                    .onFailure(throwable -> log.error("Error for getting email", throwable))
                                    .getOrElse("");
                            String subject = Try.of(() -> resourceReader.getSubject(getFirmName(trade)))
                                            .onFailure(throwable -> log.error("Error for getting subject", throwable))
                                            .getOrElse("");
                            String text = Try.of(() -> resourceReader.getMailMessage(getFIO(trade)))
                                            .onFailure(throwable -> log.error("Error for getting text", throwable))
                                            .getOrElse("");
                            if (!subject.equals("") && !text.equals("") && !mail.equals("")) {
                                SimpleMailMessage mess = getMimeMessage(mail, subject, text);
                                result.add(mess);
                            } else {
                                if (subject.equals("")){
                                    subErrors.getAndIncrement();
                                }
                                if (mail.equals("")){
                                    mailErrors.getAndIncrement();
                                }
                                if (text.equals("")){
                                    textErrors.getAndIncrement();
                                }
                                log.error("Error creating message for trade egrul: {}",
                                        trade.getEgrul() == null ? "null" : trade.getEgrul().toString()
                                );
                                log.error("Mail ={},Subject = {}, Text =\n{}",mail, subject, text);
                            }
                        }).getOrNull()
                );
        log.info("For create prepare: {}", tradeResponses.length);
        log.info("Errors for data: text={}, mail={}, subject={}", textErrors.get(), mailErrors.get(), subErrors.get());
        log.info("Create {} messages for sending", result.size());
        return result;
    }

    @NonNull
    private SimpleMailMessage getMimeMessage(@NonNull String to, @NonNull String subject, @NonNull String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        log.info("Create email message:From:{},To:{},Subject:{}", from, to, subject);
        log.debug("Message:\n{}", text);
        return message;
    }


    @NonNull
    private String getFIO(@NonNull GosbaseTradeResponse response) {
        String fio;
        if (response.getEgrul() == null || response.getEgrul().getFio() == null || response.getEgrul().getFio().isBlank()) {
            fio = getFirmName(response);
            if (fio.contains("ИП")) {
                fio = fio.replace("ИП ", "").replace("\"", "");
            } else {
                return "";
            }
        }
        else fio = response.getEgrul().getFio();
        final String[] result = {""};
        List<String> fios = List.of(fio.split(" "));
        List<String> fiosLower= new ArrayList<>();
        fios.forEach(name -> fiosLower.add(name.toLowerCase(Locale.ROOT)));
        fiosLower.forEach(name ->
                result[0] += Try.of(()->
                        name.replaceFirst(name.substring(0,1),
                                name.substring(0,1).toUpperCase(Locale.ROOT)).concat(" ")
                ).getOrElse(""));

        log.debug("get fio: {}", result[0]);
        return result[0];
    }

    @NonNull
    private String getFirmName(@NonNull GosbaseTradeResponse response) {
        String firmName = response.getEgrul() == null
                ||  response.getEgrul().getShortname() == null
                || response.getEgrul().getShortname().isBlank()
                ? "победителя конкурса"
                : response.getEgrul().getShortname();
        log.debug("get firm name: {}",firmName);
        return firmName;
    }

    @NonNull
    private String getEmail(@NonNull GosbaseTradeResponse response) {
        log.debug("get email: {}", response.getEgrul().getContacts().getActualEmailString());
        return response
                .getEgrul()
                .getContacts()
                .getActualEmailString();
    }
}
