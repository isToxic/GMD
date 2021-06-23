package is.toxic.GMD.service;

import is.toxic.GMD.DTO.Email;
import is.toxic.GMD.DTO.GosbaseTradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GosbaseService {
    private final RestTemplate template;
    private final String SORT = "last_update";

    @Value("${GMD.apikey}")
    private String apikey;


    public GosbaseTradeResponse[] getTradesPage(int page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://gosbase.ru/v1/api/online/");
        builder.queryParam("page", page);
        builder.queryParam("key", apikey);
        builder.queryParam("sort", SORT);

        RequestEntity<?> requestEntity = RequestEntity
                .method(HttpMethod.GET, builder.build().toUri())
                .build();

        return template.exchange(requestEntity, GosbaseTradeResponse[].class)
                .getBody();
    }

    @NonNull
    public String getFIO(@NonNull GosbaseTradeResponse response){
        return response
                .getEgrul()
                .getFio();
    }

    @NonNull
    public String getEmail(@NonNull GosbaseTradeResponse response){
        return response
                .getEgrul()
                .getContacts()
                .getActualEmailString();
    }

}
