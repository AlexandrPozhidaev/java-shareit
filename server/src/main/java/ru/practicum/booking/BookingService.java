package ru.practicum.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.BookingDto;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    Page<BookingDto> getUserBookings(Long userId, String state, Pageable pageable);

    Page<BookingDto> getOwnerBookings(Long ownerId, String state, Pageable pageable);

}
