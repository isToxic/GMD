package is.toxic.GMD.service;

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
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(resourceReader.getSubject(firmName));
        message.setText(resourceReader.getMailMessage(fio));
        log.info("Send email message:\nFrom:{},\nTo:{},\nSubject:{},\nMessage:{}", from, to, message.getSubject(), message.getText());
        emailSender.send(message);
    }
}
