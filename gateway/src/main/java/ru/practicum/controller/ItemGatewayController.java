package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.ValidationException;

import static ru.practicum.Header.HEADER;

@Controller
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

        log.info("Создание вещи для пользователя ID: {}, item: name='{}'", userId, itemDto.getName());
        try {
            return itemClient.createItem(userId, itemDto);
        } catch (ClassCastException e) {
            log.error("Неожиданный ответ при создании вещи пользователя {}", userId, e);
            throw new ValidationException("Некорректный формат ответа сервера");
        }
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable @Positive Long itemId,
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestBody ItemDto itemDto) {

        log.info("Обновление вещи ID: {} пользователя ID: {}", itemId, userId);
        itemDto.setId(itemId);

        try {
            return itemClient.updateItem(userId, itemId, itemDto);
        } catch (ClassCastException e) {
            log.error("Неожиданный тип ответа при обновлении вещи {}", itemId, e);
            throw new ValidationException("Некорректный формат ответа сервера");
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable @Positive Long itemId,
            @RequestHeader(HEADER) @Positive Long userId) {

        log.info("Получение вещи ID: {} для пользователя ID: {}", itemId, userId);



         try {
             return itemClient.getItemById(userId, itemId);
        } catch (ClassCastException e) {
            log.error("Неожиданный тип ответа при получении вещи {}", itemId, e);
            throw new ValidationException("Некорректный формат ответа сервера");
        }
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader(HEADER) @Positive Long userId) {

        log.info("Получение вещей владельца ID: {}, from: {}, size: {}", userId);

        try {
            return itemClient.getAllItemsByOwner(userId);
        } catch (ClassCastException e) {
            log.error("Неожиданный тип ответа при получении вещей пользователя {}", userId, e);
            throw new ValidationException("Некорректный формат ответа сервера");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text) {

        log.info("Поиск элементов: '{}'", text);

        try {
            return itemClient.searchItems(text);
        } catch (ClassCastException e) {
            log.error("Неожиданный формат ответа поиска: '{}'", text, e);
            throw new ValidationException("Недопустимый формат ответа на запрос");
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
}
