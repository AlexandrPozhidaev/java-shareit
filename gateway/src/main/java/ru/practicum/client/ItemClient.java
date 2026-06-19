package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .build());
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsByOwner(Long userId) {
        Map<String, Object> parameters = new HashMap<>();
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("text", text);
        return get("/search", null, parameters);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long authorId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", authorId, commentDto);
    }
}
