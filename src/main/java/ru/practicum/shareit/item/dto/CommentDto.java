package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id; // уникальный идентификатор комментария;

    private String text; // содержимое комментария;

    private Long authorId; // id автора комментария;

    private String authorName; // имя автора

    private LocalDateTime created; // дата создания комментария

}
