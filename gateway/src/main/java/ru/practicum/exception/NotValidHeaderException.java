package ru.practicum.exception;

public class NotValidHeaderException extends RuntimeException {
    public NotValidHeaderException(String message) {
        super(message);
    }
}
