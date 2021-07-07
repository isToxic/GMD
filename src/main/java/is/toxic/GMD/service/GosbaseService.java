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

import java.util.*;

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

}
