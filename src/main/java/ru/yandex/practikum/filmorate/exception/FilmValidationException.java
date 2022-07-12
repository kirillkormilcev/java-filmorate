package ru.yandex.practikum.filmorate.exception;

public class FilmValidationException extends RuntimeException {

    public FilmValidationException(final String message) {
        super(message);
    }
}
