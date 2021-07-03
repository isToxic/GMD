package is.toxic.GMD.util;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(classes = {ResourceReader.class})
public class ResourceReaderTest {
    private final ResourceReader resourceReader;

    @Value("${GMD.mail-subject}")
    private String mailSubject;

    @Test
    public void getMailMessageTest() {
        String f = RandomString.make().replace("I", "6").replace("O", "8");
        String i = RandomString.make().replace("I", "6").replace("O", "8");
        String o = RandomString.make().replace("I", "6").replace("O", "8");
        String fio = f.concat(" ").concat(i).concat(" ").concat(o);
        String result = resourceReader.getMailMessage(fio);
        String expected = new String(
                Try.of(() -> Files.readAllBytes(Path.of("config/offer.txt"))).get(),
                StandardCharsets.UTF_8
        );
        expected = expected.replace("I", i.concat(" "));
        expected = expected.replace("O", o);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getSubjectTest() {
        String firmName = RandomString.make();
        String result = resourceReader.getSubject(firmName);
        String expected = mailSubject.replace("FIRM_NAME", firmName);
        Assertions.assertEquals(expected, result);
    }
}
