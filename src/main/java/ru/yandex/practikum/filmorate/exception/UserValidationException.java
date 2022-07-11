package ru.yandex.practikum.filmorate.exception;

public class UserValidationException extends RuntimeException {

    public UserValidationException(final String message) {
        super(message);
    }
}
