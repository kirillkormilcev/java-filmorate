package ru.yandex.practikum.filmorate.exception;

public class GenreValidationException extends RuntimeException {
    public GenreValidationException(final String message) {
        super(message);
    }
}
