package is.toxic.GMD.service;

import is.toxic.GMD.DTO.GosbaseTradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GosbaseService {
    private final RestTemplate template;
    private final String SORT = "last_update";

    @Value("${GMD.gosbase-url}")
    private String url;

    @Value("${GMD.apikey}")
    private String apikey;


    public GosbaseTradeResponse[] getTradesPage(int page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder.queryParam("page", page);
        builder.queryParam("key", apikey);
        builder.queryParam("sort", SORT);

        RequestEntity<?> requestEntity = RequestEntity
                .method(HttpMethod.GET, builder.build().toUri())
                .build();

        log.info("getting gosbase info page: {}", page);
        GosbaseTradeResponse[] result = template.exchange(requestEntity, GosbaseTradeResponse[].class).getBody();
        log.info("get {} trades", Objects.requireNonNull(result).length);
        return result;
    }

    @NonNull
    public String getFIO(@NonNull GosbaseTradeResponse response) {
        String fio = response.getEgrul().getFio() == null
                || response.getEgrul().getFio().isBlank()
                ?
                getFirmName(response).replace("ИП ", "").replace("\"", "")
                :
                response.getEgrul().getFio();
        final String[] result = {""};
        String[] fios = fio.split(" ");
        Arrays.stream(fios).forEach(name -> result[0] += name.toLowerCase(Locale.ROOT).replaceFirst(name.substring(0,1), name.substring(0,1).toUpperCase(Locale.ROOT)).concat(" "));
        log.info("get fio: {}", result[0]);
        return result[0];
    }

    @NonNull
    public String getFirmName(@NonNull GosbaseTradeResponse response) {
        log.info("get firm name: {}", response.getEgrul().getShortname());
        return response
                .getEgrul()
                .getShortname();
    }

    @NonNull
    public String getEmail(@NonNull GosbaseTradeResponse response) {
        log.info("get email: {}", response.getEgrul().getContacts().getActualEmailString());
        return response
                .getEgrul()
                .getContacts()
                .getActualEmailString();
    }
}
