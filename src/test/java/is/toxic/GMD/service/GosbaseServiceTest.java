package is.toxic.GMD.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import is.toxic.GMD.DTO.Contacts;
import is.toxic.GMD.DTO.Egrul;
import is.toxic.GMD.DTO.Email;
import is.toxic.GMD.DTO.GosbaseTradeResponse;
import is.toxic.GMD.GosbaseMailDistributorApplication;
import lombok.RequiredArgsConstructor;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@AutoConfigureWebClient
@AutoConfigureMockRestServiceServer
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(value = "file:src/test/resources/application.yml")
@SpringBootTest(classes = {GosbaseMailDistributorApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class GosbaseServiceTest {
    private final GosbaseService gosbaseService;
    private final RestTemplate template;
    private MockRestServiceServer server;
    private final String SORT = "last_update";

    @Value("${GMD.apikey}")
    private String apikey;

    @Value("${GMD.gosbase-url}")
    private String url;

    @Test
    public void getEmailTest() {
        GosbaseTradeResponse response = generateResponse();
        String testEmail = response
                .getEgrul()
                .getContacts()
                .getEmails()
                .get(0)
                .getEmail();

        String result = gosbaseService.getEmail(response);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testEmail, result);
    }

    @Test
    public void getFioTest() {
        GosbaseTradeResponse response = generateResponse();
        String testFio = response.getEgrul().getFio();
        String result = gosbaseService.getFIO(response);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testFio, result);
    }

    @Test
    public void getFirmNameTest() {
        GosbaseTradeResponse response = generateResponse();
        String testName = response.getEgrul().getShortname();
        String result = gosbaseService.getFirmName(response);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testName, result);
    }

    @Test
    public void getTradesPageTest() throws JsonProcessingException {
        GosbaseTradeResponse resp = generateResponse();
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(template);
        server = MockRestServiceServer.createServer(gateway);
        String response = new ObjectMapper().findAndRegisterModules().writeValueAsString(List.of(resp));
        int pageNum = new Random().nextInt();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder.queryParam("page", pageNum);
        builder.queryParam("key", apikey);
        builder.queryParam("sort", SORT);

        server.expect(ExpectedCount.once(), requestTo(builder.toUriString()))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        GosbaseTradeResponse result = gosbaseService.getTradesPage(pageNum)[0];
        server.verify();
        Assertions.assertEquals(resp, result);
    }

    @NonNull
    private GosbaseTradeResponse generateResponse(){
        GosbaseTradeResponse response = new GosbaseTradeResponse();
        Egrul egrul = new Egrul();
        Contacts contacts = new Contacts();
        Email email = new Email();
        email.setEmail(RandomString.make().concat("@gmail.com"));
        email.setActual_date(LocalDate.now());
        contacts.setEmails(List.of(email));
        egrul.setFio(RandomString.make());
        egrul.setContacts(contacts);
        egrul.setShortname(RandomString.make());
        response.setEgrul(egrul);
        return response;
    }

}
