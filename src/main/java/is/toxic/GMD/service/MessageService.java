package is.toxic.GMD.service;

import io.vavr.control.Try;
import is.toxic.GMD.DTO.GosbaseTradeResponse;
import is.toxic.GMD.entity.MailEntity;
import is.toxic.GMD.repository.MailsRepository;
import is.toxic.GMD.util.ResourceReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MessageService {
    private final ResourceReader resourceReader;
    private final MailsRepository repository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${GMD.max-in-cache}")
    private long maxInCache;

    @NonNull
    public void prepareMessagesForSend(GosbaseTradeResponse... tradeResponses) {
        Arrays.stream(tradeResponses).forEach(trade -> Try.runRunnable(() -> prepareMessageData(trade)).getOrNull());
        log.info("For create prepared: {}", tradeResponses.length);
    }

    public MimeMessage getMessage(String mail, String text, String subject) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        Try.of(() -> new MimeMessageHelper(mimeMessage, "utf-8"))
                .andThenTry(helper -> helper.setText(text, true))
                .andThenTry(helper -> helper.setTo(mail))
                .andThenTry(helper -> helper.setSubject(subject))
                .andThenTry(helper -> helper.setFrom(from)).get();
        return mimeMessage;
    }

    public void prepareMessageData(GosbaseTradeResponse gosbaseTradeResponse) {
        String mail = Try.of(() -> getEmail(gosbaseTradeResponse))
                .onFailure(throwable -> log.error("Error for getting email", throwable))
                .getOrElse("");
        if (mail.equals("")) {
            return;
        }
        if (repository.existsEmailsByEmailAndUnsubscribe(mail, true)) {
            log.info("Mail: {} in unsubscribe list, skip message", mail);
            return;
        }
        if (repository.existsEmailsByEmailAndSendYet(mail, true)) {
            log.info("Mail: {} in send yet list, skip message", mail);
            return;
        }
        long taskCount = repository.countByUnsubscribe(false);
        if (taskCount >= maxInCache){
            log.info("Mail: {} not add. Reason: db has max tasks for send. Max:{}, actual:{}, ", mail, maxInCache, taskCount);
            return;
        }
        createAndSaveMessageData(getFirmName(gosbaseTradeResponse), getFIO(gosbaseTradeResponse), mail);
    }

    @Nullable
    private void createAndSaveMessageData(String firmName, String fio, String mail) {

        String subject = Try.of(() -> resourceReader.getSubject(firmName))
                .onFailure(throwable -> log.error("Error for getting subject", throwable))
                .getOrElse("");

        String text = Try.of(() -> resourceReader.getMailMessage(fio, mail))
                .onFailure(throwable -> log.error("Error for getting text", throwable))
                .getOrElse("");

        if (subject.equals("")) {
            log.error("Error for creating message on subject");
            return;
        }
        if (mail.equals("")) {
            log.error("Error for creating message on email");
            return;
        }
        if (text.equals("")) {
            log.error("Error for creating message on text");
            return;
        }

        MailEntity forSave = new MailEntity();
        forSave.setAddingData(Instant.now());
        forSave.setUnsubscribe(false);
        forSave.setEmail(mail);
        forSave.setFio(fio);
        forSave.setOrganisation(firmName);
        forSave.setMessage(text);
        forSave.setSubject(subject);
        forSave.setSendYet(false);

        if (!repository.existsById(mail)) {
            Try.run(() -> repository.save(forSave))
                    .onSuccess(unused -> log.info("Offer with email: {}, fio: {}, organisation: {}, saved", mail, fio, firmName))
                    .getOrNull();
        } else {
            log.info("mail: {} exist in db", mail);
        }
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