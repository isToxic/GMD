package is.toxic.GMD.util;

import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class ResourceReader {

    @Value("${GMD.mail-subject}")
    private String mailSubject;

    @NonNull
    public String getMailMessage(@NonNull String fio) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("file:config/offer.txt");
        String[] fioArray = fio.split(" ");
        return asString(resource).replace("IO", fioArray[1].concat(" ").concat(fioArray[2]));
    }

    public String getSubject(String firmName) {
        return mailSubject.replace("FIRM_NAME", firmName);
    }

    @NonNull
    private String asString(@NonNull Resource resource) {
        return Try.of(() -> FileCopyUtils.copyToString(
                Try.of(() -> new InputStreamReader(resource.getInputStream(), UTF_8)).get())
        ).get();
    }
}