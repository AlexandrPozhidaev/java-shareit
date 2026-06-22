package ru.practicum.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.exception.NotValidHeaderException;

import static ru.practicum.Header.HEADER;

@Controller
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestGatewayController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestGatewayController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader(HEADER) long requestorId,
            @Valid @RequestBody ItemRequestDto requestDto) {
                return itemRequestClient.createRequest(requestorId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(HEADER) long userId) {
        validateUserIdHeader(userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        return itemRequestClient.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId) {
        return itemRequestClient.getRequestById(requestId);
    }

    private void validateUserIdHeader(Long userId) {
        if (userId == null) {
            throw new NotValidHeaderException("Заголовок " + HEADER + " обязателен для всех запросов");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }
    }
}
