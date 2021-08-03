package is.toxic.GMD.util;

import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
    public String getMailMessage(@NonNull String fio, @NonNull String email) {
        if (fio.isBlank()){
            return offerText().replace("IO", "уважаемый директор, предпрениматель");
        }
        String[] fioArray = fio.split(" ");
        String replaceResult = Try.of(()->fioArray[1].concat(" ").concat(fioArray[2])).getOrElse("уважаемый директор, предпрениматель");
        return offerText().replace("IO", replaceResult).replace("EMAIL", email);
    }

    public String getSubject(@NonNull String firmName) {
        return mailSubject.replace("FIRM_NAME", firmName);
    }

    @NonNull
    private String asString(@NonNull Resource resource) {
        return Try.of(() -> FileCopyUtils.copyToString(
                Try.of(() -> new InputStreamReader(resource.getInputStream(), UTF_8)).get())
        ).get();
    }


    @Bean
    public String offerText(){
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("/email/email.html");
        return asString(resource);
    }
}