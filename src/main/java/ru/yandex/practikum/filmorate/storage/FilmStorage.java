package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.Genre;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.model.user.User;

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

    /**
     * получить жанр по id
     */
    /*Genre getGenreById(int id);*/

    /**
     * получить все MPA рейтинги
     */
    List<MPA> getAllMPAs();

    /**
     * получить MPA рейтинг по id
     */
    MPA getMPAById(int id);

    /**
     * получить все жанры
     */
    /*List<Genre> getAllGenres();*/

    /**
     * геттер мапы фильмов
     */
    Map<Long, Film> getFilms();

    /**
     * геттер лайков фильма
     */
    Map<Long, Set<User>> getLikeIds();

}

