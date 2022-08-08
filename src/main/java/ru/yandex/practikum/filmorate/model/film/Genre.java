package ru.yandex.practikum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;

/** класс жанра фильма */
@Data
@Builder
public class Genre {
    private final int id;
    private final String name;
    private final String description;
}
