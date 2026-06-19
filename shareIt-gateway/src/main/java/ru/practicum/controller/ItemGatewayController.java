package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;

import static ru.practicum.Header.HEADER;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemGatewayController {
    private final ItemClient itemClient;

    public ItemGatewayController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(HEADER) @Positive Long userId,
            @Valid @RequestBody ItemDto itemDto) {

        log.info("Creating item for user ID: {}, item: name='{}'", userId, itemDto.getName());
        try {
            return itemClient.createItem(userId, itemDto);
        } catch (ClassCastException e) {
            log.error("Unexpected response type when creating item for user {}", userId, e);
            throw new ValidationException("Invalid response format from server");
        }
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable @Positive Long itemId,
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestBody ItemDto itemDto) {

        log.info("Updating item ID: {} for user ID: {}", itemId, userId);
        itemDto.setId(itemId);

        try {
            return itemClient.updateItem(userId, itemId, itemDto);
        } catch (ClassCastException e) {
            log.error("Unexpected response type when updating item {}", itemId, e);
            throw new ValidationException("Invalid response format from server");
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable @Positive Long itemId,
            @RequestHeader(HEADER) @Positive Long userId) {

        log.info("Getting item ID: {} for user ID: {}", itemId, userId);



         try {
             return itemClient.getItemById(userId, itemId);
        } catch (ClassCastException e) {
            log.error("Unexpected response type when getting item {}", itemId, e);
            throw new ValidationException("Invalid response format from server");
        }
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader(HEADER) @Positive Long userId) {

        log.info("Getting items for owner ID: {}, from: {}, size: {}", userId);

        try {
            return itemClient.getAllItemsByOwner(userId);
        } catch (ClassCastException e) {
            log.error("Unexpected response type when getting items for user {}", userId, e);
            throw new ValidationException("Invalid response format from server");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text) {

        log.info("Searching items with text: '{}', from: {}, size: {}", text);

        try {
            return itemClient.searchItems(text);
        } catch (ClassCastException e) {
            log.error("Unexpected search response format for text: '{}'", text, e);
            throw new ValidationException("Invalid search response format");
        }
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable @Positive Long itemId,
            @RequestHeader(HEADER) @Positive Long authorId,
            @RequestBody @Valid CommentDto commentDto) {

        log.info("Adding comment for item ID: {}, author ID: {}", itemId, authorId);
        try {
            return itemClient.addComment(itemId, authorId, commentDto);
        } catch (ClassCastException e) {
            log.error("Unexpected response type when adding comment to item {}", itemId, e);
            throw new ValidationException("Invalid comment response format");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T extractBody(ResponseEntity<Object> response, Class<T> type) {
        if (response.getBody() == null) {
            return null;
        }
        if (!type.isInstance(response.getBody())) {
            throw new ClassCastException(
                    "Expected type: " + type.getSimpleName() +
                            ", but got: " + response.getBody().getClass().getSimpleName());
        }
        return (T) response.getBody();
    }
}
