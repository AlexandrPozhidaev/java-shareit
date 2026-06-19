package ru.practicum.booking;

import ru.practicum.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(Long userId, String state);

    List<BookingDto> getOwnerBookings(Long ownerId, String state);

}
