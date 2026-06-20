package ru.practicum.exception;

import org.hibernate.LazyInitializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeader(MissingRequestHeaderException ex) {
        log.warn("Отсутствует заголовок: {}", ex.getHeaderName());
        return new ErrorResponse(
                "Отсутствует заголовок: " + ex.getHeaderName(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("Ошибка валидации: {}", errors);
        ErrorResponse errorResponse = new ErrorResponse(
                "Валидация не пройдена: " + String.join("; ", errors),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyUsedException ex) {
        log.warn("Email уже используется: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(NotFoundException ex) {
        log.warn("Пользователь не найден: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Пользователь не найден", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException ex) {
        log.warn("Доступ отклонен: {}", ex.getMessage());
        return new ErrorResponse("Доступ отклонен: " + ex.getMessage(), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("HTTP error {}: {}", ex.getStatusCode(), ex.getReason());
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getReason() != null ? ex.getReason() : "Произошла ошибка",
                ex.getStatusCode().value()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception ex) {
        String errorMessage = "Внутренняя ошибка сервера: " + ex.getMessage();
        log.error(errorMessage, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<ErrorResponse> handleLazyInitializationException(LazyInitializationException ex) {
        log.error("Ошибка загрузки связанных сущностей: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "Ошибка загрузки данных: связанные сущности не загружены",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
