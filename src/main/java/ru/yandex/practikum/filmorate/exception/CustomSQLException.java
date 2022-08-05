package ru.yandex.practikum.filmorate.exception;

public class CustomSQLException extends RuntimeException {
    public CustomSQLException(final String message) {
        super(message);
    }
}
