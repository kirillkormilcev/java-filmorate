package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Genre;

import java.util.List;

public interface GenreStorage {

    /**
     * получить жанр по id
     */
    Genre getGenreById(int id);

    /**
     * получить все жанры
     */
    List<Genre> getAllGenres();

    /**
     * список id жанров фильма по его id
     */
    List<Integer> getGenreIdsByFilmId(long id);
}
