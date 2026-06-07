package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingDto bookingDto) {
        BookingDto savedBooking = bookingService.createBooking(bookingDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam Boolean approved) {
        BookingDto updatedBooking = bookingService.approveBooking(bookingId, ownerId, approved);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingDto bookingDto = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.getUserBookings(userId, state);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.getOwnerBookings(ownerId, state);
        return ResponseEntity.ok(bookings);
    }
}
