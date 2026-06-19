package ru.practicum.dto;

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

    @NotNull(groups = OnCreate.class)
    @Email(groups = OnCreate.class)
    private String email;
}