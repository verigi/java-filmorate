package ru.yandex.practicum.filmorate.exception;

public class IncorrectUserDetailsException extends RuntimeException {
    public IncorrectUserDetailsException(final String message) {
        super(message);
    }
}
