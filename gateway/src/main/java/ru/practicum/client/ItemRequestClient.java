package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.ItemRequestDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(long requestorId, ItemRequestDto requestDto) {
        return post("", requestorId, requestDto);
    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests() {
        Map<String, Object> parameters = new HashMap<>();
        return get("/all", null, parameters);
    }

    public ResponseEntity<Object> getRequestById(long requestId) {
        return get("/" + requestId);
    }
}
