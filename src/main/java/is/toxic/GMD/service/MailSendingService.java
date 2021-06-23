package is.toxic.GMD.service;

import is.toxic.GMD.util.ResourceReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MailSendingService {
    private final JavaMailSender emailSender;
    private final ResourceReader resourceReader;

    public void sendMail(String to, String subject, String fio) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(resourceReader.getMailMessage(fio));
        emailSender.send(message);
    }
}
