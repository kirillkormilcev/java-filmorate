package ru.yandex.practikum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practikum.filmorate.model.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class UserErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        //log;
        return new ErrorResponse(e.getMessage());
    }
}
