package ru.yandex.practikum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    public ErrorResponse(String message) {
        this.message = message;
    }

    String message;
}
