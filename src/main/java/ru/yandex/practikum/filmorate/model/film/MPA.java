package ru.yandex.practikum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;

/** класс жанра фильма */
@Data
@Builder
public class MPA {
    private final int MPAId;
    private final String MPAName;
    private final String description;
}
