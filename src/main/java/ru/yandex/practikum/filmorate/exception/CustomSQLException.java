package ru.yandex.practikum.filmorate.exception;

public class CustomSQLException extends RuntimeException {
    public CustomSQLException(final String message) {
        super(message);
    }

    public CustomSQLException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
