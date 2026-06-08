package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());

        Item item = booking.getItem();
        if (item != null) {
            dto.setItemId(item.getId());
            dto.setItem(ItemMapper.toItemDtoForBooking(item));
        } else {
            dto.setItemId(null);
            dto.setItem(null);
        }

        User booker = booking.getBooker();
        if (booker != null) {
            dto.setBookerId(booker.getId());
            dto.setBooker(UserMapper.toUserDto(booker));
        } else {
            dto.setBookerId(null);
            dto.setBooker(null);
        }

        dto.setStatus(booking.getStatus());
        return dto;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus() != null ?
                bookingDto.getStatus() : BookingStatus.WAITING);
        return booking;
    }
}
