package ru.yandex.practikum.filmorate.model.film;

import lombok.Data;

@Data
public class Like {
    private final long id;
    private final long filmId;
    private final long userId;
}
