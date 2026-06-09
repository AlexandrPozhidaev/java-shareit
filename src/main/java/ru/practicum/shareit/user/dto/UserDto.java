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

    public interface OnCreate {}

    public interface OnUpdate {}

    private Long id;

    private String name;

    @NotNull(message = "Email must not be null", groups = OnCreate.class)
    @Email(message = "Формат электронной почты должен соответствовать требованиям", groups = OnCreate.class)
    private String email;
}