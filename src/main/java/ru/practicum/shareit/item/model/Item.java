package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор вещи;

    @Column(name = "name", nullable = false)
    private String name; // краткое название;

    @Column(name = "description")
    private String description; // развёрнутое описание;

    @Column(name = "is_available", nullable = false)
    private Boolean available; // статус о том, доступна или нет вещь для аренды;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // владелец вещи;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request; // если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос.
}