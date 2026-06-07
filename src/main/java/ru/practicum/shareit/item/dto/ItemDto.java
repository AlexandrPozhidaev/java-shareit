package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id; // уникальный идентификатор вещи;

    @NotBlank(message = "Имя не может быть пустым")
    private String name; // краткое название;

    @NotBlank(message = "Описание не может быть пустым")
    private String description; // развёрнутое описание;

    @NotNull(message = "Поле available обязательно для заполнения")
    private Boolean available; // статус о том, доступна или нет вещь для аренды;

    private Long owner; // владелец вещи;

    private Long request; // если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос.

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;
}
