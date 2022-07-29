package ru.yandex.practikum.filmorate.model.film;

import lombok.Data;

@Data
public class FilmGenres {
    private final long filmGenresId;
    private final long filmId;
    private final long genreId;
}
