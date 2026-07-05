package ru.practicum.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    @NotNull
    @Positive
    private long itemId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    @AssertTrue(message = "Дата окончания должна быть позже даты начала")
    public boolean isValidDateRange() {
        return end != null && start != null && end.isAfter(start);
    }
}