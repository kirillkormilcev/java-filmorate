package ru.yandex.practikum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practikum.filmorate.exception.FilmValidationException;
import ru.yandex.practikum.filmorate.exception.IncorrectRequestParamException;
import ru.yandex.practikum.filmorate.exception.NotFoundException;
import ru.yandex.practikum.filmorate.exception.UserValidationException;
import ru.yandex.practikum.filmorate.model.ErrorResponse;
import ru.yandex.practikum.filmorate.service.FilmService;
import ru.yandex.practikum.filmorate.service.UserService;

@RestControllerAdvice({"ru.yandex.practikum.filmorate.controller", "ru.yandex.practikum.filmorate.service"})
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Объект(-ы) не найден(-ы).", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFilmValidationException(final FilmValidationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Не корректно(-ы)е поле(-я) фильма.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserValidationException(final UserValidationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Не корректно(-ы)е поле(-я) пользователя.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectRequestParamException(final IncorrectRequestParamException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Не корректный параметр запроса.", e.getMessage());
    }
}
