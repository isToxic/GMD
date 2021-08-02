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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MessageService {

    private final ResourceReader resourceReader;

    @Value("${spring.mail.username}")
    private String from;

    public SimpleMailMessage getMimeMessage(GosbaseTradeResponse gosbaseTradeResponse, AtomicInteger subErrors,
                                            AtomicInteger mailErrors, AtomicInteger textErrors) {
        String mail = Try.of(() -> getEmail(gosbaseTradeResponse))
                .onFailure(throwable -> log.error("Error for getting email", throwable))
                .getOrElse("");
        String subject = Try.of(() -> resourceReader.getSubject(getFirmName(gosbaseTradeResponse)))
                .onFailure(throwable -> log.error("Error for getting subject", throwable))
                .getOrElse("");
        String text = Try.of(() -> resourceReader.getMailMessage(getFIO(gosbaseTradeResponse)))
                .onFailure(throwable -> log.error("Error for getting text", throwable))
                .getOrElse("");
        if (!subject.equals("") && !text.equals("") && !mail.equals("")) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(mail);
            message.setSubject(subject);
            message.setText(text);
            log.info("Create email message:From:{},To:{},Subject:{}", from, mail, subject);
            log.debug("Message:\n{}", text);
            return message;
        } else {
            if (subject.equals("")) {
                subErrors.getAndIncrement();
            }
            if (mail.equals("")) {
                mailErrors.getAndIncrement();
            }
            if (text.equals("")) {
                textErrors.getAndIncrement();
            }
            log.error("Error creating message for trade egrul: {}",
                    gosbaseTradeResponse.getEgrul() == null ? "null" : gosbaseTradeResponse.getEgrul().toString()
            );
            log.error("mail ={}, subject = {}, has text =\n{}", mail, subject, StringUtils.hasText(text));
        }
        return null;
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