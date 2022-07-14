package ru.yandex.practikum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practikum.filmorate.exception.FilmValidationException;
import ru.yandex.practikum.filmorate.exception.IncorrectRequestParamException;
import ru.yandex.practikum.filmorate.exception.NotFoundException;
import ru.yandex.practikum.filmorate.exception.UserValidationException;
import ru.yandex.practikum.filmorate.model.ErrorResponse;

@RestControllerAdvice({"ru.yandex.practikum.filmorate.controller", "ru.yandex.practikum.filmorate.service"})
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("Объект(-ы) не найден(-ы).", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleFilmValidationException(final FilmValidationException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>( new ErrorResponse("Не корректно(-ы)е поле(-я) фильма.", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserValidationException(final UserValidationException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>( new ErrorResponse("Не корректно(-ы)е поле(-я) пользователя.", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIncorrectRequestParamException(final IncorrectRequestParamException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>( new ErrorResponse("Не корректный параметр запроса.", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
