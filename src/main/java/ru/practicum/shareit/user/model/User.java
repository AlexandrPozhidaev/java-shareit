package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор пользователя;

    @Column(name = "name", nullable = false)
    private String name; // имя или логин пользователя;

    @Column(name = "email", unique = true)
    private String email; // адрес электронной почты
}
