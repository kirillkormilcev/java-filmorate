package ru.yandex.practikum.filmorate.exception;

public class IncorrectRequestParamException extends RuntimeException {
    public IncorrectRequestParamException(final String message) {
        super(message);
    }
}
