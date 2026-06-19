package ru.practicum.dto;

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

    private String email;
}