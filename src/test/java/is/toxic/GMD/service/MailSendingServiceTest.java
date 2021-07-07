package is.toxic.GMD.service;


import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.vavr.control.Try;
import is.toxic.GMD.GosbaseMailDistributorApplication;
import lombok.RequiredArgsConstructor;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(value = "file:src/test/resources/application.yml")
@SpringBootTest(classes = {GosbaseMailDistributorApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MailSendingServiceTest {
    private static GreenMail greenMail;
    private final MailSendingService sendingService;
    private final GosbaseService gosbaseService;
    private final static String username = RandomString.make().concat("@gmail.com");

    @BeforeAll
    static void setUpAll() {
        String password = RandomString.make();
        greenMail = new GreenMail(ServerSetupTest.SMTP_IMAP);
        greenMail.start();
        GreenMailUser user = greenMail.setUser(username, username, password);
        System.setProperty("spring.mail.host", "localhost");
        System.setProperty("spring.mail.port", String.valueOf(greenMail.getSmtp().getPort()));
        System.setProperty("spring.mail.username", username);
        System.setProperty("spring.mail.password", password);
    }
//
//    @Test
//    public void sendMailTest() {
//        String to = RandomString.make().concat("@gmail.com");
//        String firmName = RandomString.make();
//        String fio = RandomString.make().concat(" ").concat(RandomString.make()).concat(" ").concat(RandomString.make());
//        sendingService.sendMail(to, firmName, fio);
//
//        MimeMessage result = greenMail.getReceivedMessages()[0];
//
//        String textResult = (String) Try.of(result::getContent).get();
//        String fromResult = Try.of(result::getFrom).get()[0].toString();
//        String subjectResult = Try.of(result::getSubject).get();
//        String toResult = Try.of(result::getAllRecipients).get()[0].toString();
//
//        Assertions.assertEquals(to, toResult);
//        Assertions.assertEquals(username, fromResult);
//        Assertions.assertNotNull(subjectResult);
//        Assertions.assertNotNull(textResult);
//
//        greenMail.stop();
//    }

}
