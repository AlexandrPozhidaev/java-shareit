package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.BookingClient;
import ru.practicum.client.ItemClient;
import ru.practicum.client.UserClient;
import ru.practicum.dto.*;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.ValidationException;

import java.util.List;
import java.util.Map;

import static ru.practicum.Header.HEADER;

@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingGatewayController {

    private final BookingClient bookingClient;
    private final UserClient userClient;
    private final ItemClient itemClient;

    public BookingGatewayController(BookingClient bookingClient, UserClient userClient, ItemClient itemClient) {
        this.bookingClient = bookingClient;
        this.userClient = userClient;
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(HEADER) @Positive Long userId,
            @Valid @RequestBody BookItemRequestDto bookingDto) {
        log.info("Received create booking request for user ID: {}", userId);

        ResponseEntity<Object> userResponse = userClient.getUserById(userId);
        if (userResponse.getStatusCode().is4xxClientError()) {
            throw new ValidationException("Пользователь с ID " + userId + " не найден");
        }

        ResponseEntity<Object> itemResponse = itemClient.getItemById(userId, bookingDto.getItemId());
        if (itemResponse.getStatusCode().is4xxClientError()) {
            throw new ValidationException("Вещь с ID " + bookingDto.getItemId() + " не найдена");
        }

        ItemDto itemDto = (ItemDto) itemResponse.getBody();
        if (!itemDto.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (!bookingDto.isValid()) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }

        checkBookingOverlap(bookingDto, userId);

        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(HEADER) @Positive Long ownerId,
            @RequestParam boolean approved) {
        log.info("Received approve booking request for booking ID: {}, owner ID: {}, approved: {}",
                bookingId, ownerId, approved);

        ResponseEntity<Object> bookingResponse = bookingClient.getBookingById(ownerId, bookingId);
        if (bookingResponse.getStatusCode().is4xxClientError()) {
            throw new ValidationException("Бронирование с ID " + bookingId + " не найдено");
        }

        BookingDto booking = (BookingDto) bookingResponse.getBody();

        if (!booking.getItem().getOwner().equals(ownerId)) {
            throw new AccessDeniedException("Пользователь не является владельцем вещи");
        }

        if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
            throw new ValidationException("Нельзя подтвердить/отклонить отменённое бронирование");
        }
        if (approved && BookingStatus.APPROVED.equals(booking.getStatus())) {
            throw new ValidationException("Бронирование уже подтверждено");
        }

        Map<String, Object> parameters = Map.of("approved", approved);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(HEADER) @Positive Long userId) {
        log.info("Received get booking request for booking ID: {}, user ID: {}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Received get user bookings request for user ID: {}, state: {}, from: {}, size: {}",
                userId, state, from, size);
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(HEADER) @Positive Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Received get owner bookings request for owner ID: {}, state: {}, from: {}, size: {}",
                ownerId, state, from, size);

        try {
            BookingState.from(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Недопустимое значение state: " + state);
        }

        return bookingClient.getOwnerBookings(ownerId, state, from, size);
    }

    private void checkBookingOverlap(BookItemRequestDto requestDto, Long bookerId) {
        ResponseEntity<Object> existingBookingsResponse =
                bookingClient.getBookingsByItemId(requestDto.getItemId());

        if (existingBookingsResponse.getStatusCode().is2xxSuccessful()) {
            if (existingBookingsResponse.getBody() instanceof List) {
                @SuppressWarnings("unchecked")
                List<BookingDto> existingBookings =
                        (List<BookingDto>) existingBookingsResponse.getBody();

                for (BookingDto existing : existingBookings) {
                    if (isOverlapping(requestDto, existing)) {
                        throw new ValidationException(
                                "Вещь уже забронирована на указанный период: " +
                                        "с " + existing.getStart() + " по " + existing.getEnd());
                    }
                }
            }
        }
    }


    private boolean isOverlapping(BookItemRequestDto newBooking, BookingDto existing) {
        return newBooking.getStart().isBefore(existing.getEnd()) &&
                newBooking.getEnd().isAfter(existing.getStart());
    }
}