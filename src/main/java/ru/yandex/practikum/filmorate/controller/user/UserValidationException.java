package ru.yandex.practikum.filmorate.controller.user;

class UserValidationException extends RuntimeException {

    public UserValidationException(final String message) {
        super(message);
    }
}
