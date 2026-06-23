package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.BookingClient;
import ru.practicum.dto.*;
import ru.practicum.exception.NotValidHeaderException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ru.practicum.Header.HEADER;

@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingGatewayController {

    private final BookingClient bookingClient;


    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookingGatewayController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(HEADER) @Positive Long userId,
            @Valid @RequestBody BookItemRequestDto bookingDto) {
        validateUserIdHeader(userId);
        log.info("Создание бронирования для пользователя ID: {}, item ID: {}, start: {}, end: {}",
                userId, bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getEnd());
                return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(HEADER) @Positive Long ownerId,
            @RequestParam boolean approved) {
        log.info("Received approve booking request for booking ID: {}, owner ID: {}, approved: {}",
                bookingId, ownerId, approved);
        return bookingClient.approveBooking(ownerId, bookingId, approved);

    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(HEADER) @Positive Long userId) {
        validateUserIdHeader(userId);

        log.info("Received get booking request for booking ID: {}, user ID: {}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        state = state.toUpperCase();
        if (!BookingState.from(state).isPresent()) {
            throw new IllegalArgumentException("Недопустимое состояние бронирования: " + state);
        }

        log.info("Received get user bookings request for user ID: {}, state: {}, from: {}, size: {}",
                userId, state, from, size);
        validateUserIdHeader(userId);

        ResponseEntity<Object> clientResponse = bookingClient.getUserBookings(userId, state, from, size);

        if (clientResponse.getBody() == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<BookingDto> resultList = Collections.emptyList();
        Object body = clientResponse.getBody();

        if (body instanceof Map<?, ?>) {
            Map<?, ?> mapBody = (Map<?, ?>) body;

            if (mapBody.containsKey("content")) {
                Object contentObj = mapBody.get("content");

                if (contentObj instanceof List<?>) {
                    List<?> rawList = (List<?>) contentObj;

                    @SuppressWarnings("unchecked")
                    List<BookingDto> typedList = (List<BookingDto>) rawList;
                    resultList = typedList;
                }
            }
        }
        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(HEADER) @Positive Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        state = state.toUpperCase();
        if (!BookingState.from(state).isPresent()) {
            throw new IllegalArgumentException("Недопустимое состояние бронирования: " + state);
        }

        log.info("Received get owner bookings request for owner ID: {}, state: {}, from: {}, size: {}",
                ownerId, state, from, size);
        return bookingClient.getOwnerBookings(ownerId, state, from, size);
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