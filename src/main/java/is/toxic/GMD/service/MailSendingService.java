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

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MailSendingService {
    private final JavaMailSender emailSender;
    private final ResourceReader resourceReader;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(GosbaseTradeResponse... tradeResponses) {
        Try.run(() -> emailSender.send(getMessagesForSend(tradeResponses)))
                .onFailure(throwable -> log.error("Error for sending message", throwable))
                .get();
    }

    @NonNull
    private SimpleMailMessage[] getMessagesForSend(GosbaseTradeResponse... tradeResponses){
        List<SimpleMailMessage> result = new ArrayList<>();
        Arrays.stream(tradeResponses)
                .parallel()
                .forEach(trade ->
                        Try.runRunnable(() -> {
                                    String subject = Try.of(() -> resourceReader.getSubject(getFirmName(trade)))
                                            .onFailure(throwable -> log.error("Error for getting subject", throwable))
                                            .getOrElse("");
                                    String text = Try.of(() -> resourceReader.getMailMessage(getFIO(trade)))
                                            .onFailure(throwable -> log.error("Error for getting text", throwable))
                                            .getOrElse("");
                                    if (!subject.equals("") && !text.equals("")) {
                                        SimpleMailMessage mess = getMimeMessage(getEmail(trade), subject, text);
                                        result.add(mess);
                                    } else {
                                        log.error("Error creating message for trade egrul: {}", trade.getEgrul().toString());
                                        log.error("Subject = {}, Text = /n{}", subject, text);
                                    }
                                }
                        ).getOrNull()
                );
        return result.toArray(new SimpleMailMessage[result.size()]);
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
    public String getFIO(@NonNull GosbaseTradeResponse response) {
        String fio = response.getEgrul() == null || response.getEgrul().getFio() == null
                || response.getEgrul().getFio().isBlank()
                ?
                getFirmName(response).replace("ИП ", "").replace("\"", "")
                :
                response.getEgrul().getFio();
        final String[] result = {""};
        List<String> fios = List.of(fio.split(" "));
        log.info(Arrays.toString(fios.toArray()));
        List<String> fiosLower= new ArrayList<>();
        fios.forEach(name -> fiosLower.add(name.toLowerCase(Locale.ROOT)));
        fiosLower.forEach(name -> result[0] += name.replaceFirst(name.substring(0,1), name.substring(0,1).toUpperCase(Locale.ROOT)).concat(" "));

        log.debug("get fio: {}", result[0]);
        return result[0];
    }

    @NonNull
    public String getFirmName(@NonNull GosbaseTradeResponse response) {
        String firmName = response.getEgrul() == null
                ||  response.getEgrul().getShortname() == null
                || response.getEgrul().getShortname().isBlank()
                ? "победителя конкурса"
                : response.getEgrul().getShortname();
        log.debug("get firm name: {}",firmName);
        return firmName;
    }

    @NonNull
    public String getEmail(@NonNull GosbaseTradeResponse response) {
        log.debug("get email: {}", response.getEgrul().getContacts().getActualEmailString());
        return response
                .getEgrul()
                .getContacts()
                .getActualEmailString();
    }
}
