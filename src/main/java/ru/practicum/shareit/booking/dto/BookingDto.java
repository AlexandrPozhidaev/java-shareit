package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id; //  уникальный идентификатор бронирования;

    private LocalDateTime start; // дата и время начала бронирования;

    private LocalDateTime end; // дата и время конца бронирования;

    private Long itemId; // id вещи, которую пользователь бронирует;

    private Long bookerId; // id пользователя, который осуществляет бронирование;

    private BookingStatus status; // статус бронирования

    private ItemDto item; //  вещь

    private UserDto booker; // пользователь

}
