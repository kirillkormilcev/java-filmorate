package ru.yandex.practikum.filmorate.model.film;

import lombok.Data;

/** класс лайка фильму пользователем */
@Data
public class Like {
    private final long id;
    private final long filmId;
    private final long userId;
}
