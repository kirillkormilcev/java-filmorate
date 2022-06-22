package ru.yandex.practikum.filmorate.controller.film;

class FilmValidationException extends RuntimeException {

    public FilmValidationException(final String message) {
        super(message);
    }
}
