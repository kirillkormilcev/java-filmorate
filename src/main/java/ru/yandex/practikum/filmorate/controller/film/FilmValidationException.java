package ru.yandex.practikum.filmorate.controller.film;

public class FilmValidationException extends RuntimeException {

    public FilmValidationException (final String message) {
        super(message);
    }
}
