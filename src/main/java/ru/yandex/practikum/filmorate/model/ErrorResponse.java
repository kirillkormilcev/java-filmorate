package ru.yandex.practikum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    String error;
    String message;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
