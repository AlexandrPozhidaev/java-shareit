package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id; // уникальный идентификатор пользователя;

    @NotNull(message = "Имя обязательно")
    private String name; // имя или логин пользователя;

    @NotNull(message = "Электронная почта обязательна")
    @Email(message = "Формат электронной почты должен соответствовать требованиям")
    private String email; // адрес электронной почты
}