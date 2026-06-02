package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
public class User {

    private Long id; // уникальный идентификатор пользователя;

    private String name; // имя или логин пользователя;

    private String email; // адрес электронной почты
}
