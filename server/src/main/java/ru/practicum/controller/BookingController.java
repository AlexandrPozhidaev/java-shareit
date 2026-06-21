package ru.practicum.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.BookingService;
import ru.practicum.dto.BookingDto;

import java.util.List;

import static ru.practicum.Header.HEADER;

@Controller
@RequestMapping(path = "/bookings")
public class BookingController {

        private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestBody BookingDto bookingDto) {
        BookingDto savedBooking = bookingService.createBooking(bookingDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(HEADER) @Positive Long ownerId,
            @RequestParam Boolean approved) {
        BookingDto updatedBooking = bookingService.approveBooking(bookingId, ownerId, approved);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(HEADER) @Positive Long userId) {
        BookingDto bookingDto = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader(HEADER) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        List<BookingDto> bookings = bookingService.getUserBookings(userId, state);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(
            @RequestHeader(HEADER) @Positive Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        List<BookingDto> bookings = bookingService.getOwnerBookings(ownerId, state);
        return ResponseEntity.ok(bookings);
    }
}
