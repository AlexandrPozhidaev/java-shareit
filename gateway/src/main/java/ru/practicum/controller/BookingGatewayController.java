package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.BookingClient;
import ru.practicum.dto.BookItemRequestDto;

import static ru.practicum.Header.HEADER;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingGatewayController {

    private final BookingClient bookingClient;

    public BookingGatewayController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(HEADER) @Positive Long userId,
            @Valid @RequestBody BookItemRequestDto bookingDto) {
        log.info("Received create booking request for user ID: {}", userId);
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
        return bookingClient.getOwnerBookings(ownerId, state, from, size);
    }

}
