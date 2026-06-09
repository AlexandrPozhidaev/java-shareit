package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;

    @NotNull(message = "ID автора комментария не может отсутствовать")
    private Long authorId;

    @NotBlank(message = "Имя автора комментария не может быть пустым")
    @Size(max = 30, message = "Имя автора не может быть длиннее 50 символов")
    private String authorName;

    @NotNull(message = "Дата создания комментария не может отсутствовать")
    private LocalDateTime created;

}
