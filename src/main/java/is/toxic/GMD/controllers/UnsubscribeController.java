package is.toxic.GMD.controllers;

import is.toxic.GMD.entity.MailEntity;
import is.toxic.GMD.repository.MailsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Objects;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UnsubscribeController {
    private final MailsRepository repository;

    @GetMapping("/unsubscribe/{email}")
    public ResponseEntity<Void> unsubscribe(@PathVariable("email") String email) {
        MailEntity forSave = new MailEntity();
        forSave.setEmail(email);
        forSave.setUnsubscribe(true);
        forSave.setAddingData(Instant.now());
        repository.save(forSave);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/icon.png", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getIcon() throws IOException {
        InputStream in = getClass().getResourceAsStream("/images/icon.png");
        return IOUtils.toByteArray(Objects.requireNonNull(in));
    }
    @GetMapping(value = "/logo.png", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getLogo() throws IOException {
        InputStream in = getClass().getResourceAsStream("/images/logo.png");
        return IOUtils.toByteArray(Objects.requireNonNull(in));
    }

    @GetMapping(value = "/trim_logo.png", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getTrim_logo() throws IOException {
        InputStream in = getClass().getResourceAsStream("/images/trim_logo.png");
        return IOUtils.toByteArray(Objects.requireNonNull(in));
    }
}
