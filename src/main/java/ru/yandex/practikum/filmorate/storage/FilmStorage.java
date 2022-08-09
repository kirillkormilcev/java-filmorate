package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;

import java.util.*;

public interface FilmStorage {

    /**
     * список всех фильмов
     */
    List<Film> getListOfFilms();

    /**
     * получить фильм по id
     */
    Film getFilmById(long id);

    /**
     * добавить фильм
     */
    Film addFilm(Film film);

    /**
     * обновить фильм
     */
    Film updateFilm(Film film);

    /**
     * список id всех фильмов
     */
    List<Long> getAllFilmIds();
}

