package is.toxic.GMD.service;

import io.vavr.control.Try;
import is.toxic.GMD.util.ResourceReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MailSendingService {
    private final JavaMailSender emailSender;
    private final ResourceReader resourceReader;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String firmName, String fio) {
        SimpleMailMessage message = new SimpleMailMessage();
        String subject = Try.of(() -> resourceReader.getSubject(firmName))
                .onFailure(throwable -> log.error("Error for getting subject", throwable))
                .getOrElse("");
        String text = Try.of(() -> resourceReader.getMailMessage(fio))
                .onFailure(throwable -> log.error("Error for getting text", throwable))
                .getOrElse("");
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if (!subject.equals("") && !text.equals("")) {
            log.info("Send email message:From:{},To:{},Subject:{}", from, to, message.getSubject());
            log.debug("Message:{}", message.getText());
            Try.run(() -> emailSender.send(message)).onFailure(throwable -> log.error("Error for sending message", throwable)).get();
        }
    }
}
