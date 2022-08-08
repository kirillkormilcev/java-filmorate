package ru.yandex.practikum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;

/** класс жанра фильма */
@Data
@Builder
public class Genre {
    private final int genreId;
    private final String genreName;
    private final String description;
}
