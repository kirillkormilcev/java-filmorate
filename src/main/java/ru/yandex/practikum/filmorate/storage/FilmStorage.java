package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;
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
    public Film getFilmById(long id);

    /**
     * добавить фильм
     */
    Film addFilm(Film film);

    /**
     * обновить фильм
     */
    Film updateFilm(Film film);

    /**
     * добавить лайк
     */
    void addLikeUserToFilm(long filmId, long userId);

    /**
     * удалить лайк
     */
    void removeLikeUserFromFilm(long filmId, long userId);

    /**
     * список id всех фильмов
     */
    public List<Long> getAllFilmIds();

    /**
     * геттер мапы фильмов
     */
    Map<Long, Film> getFilms();

    /**
     * геттер лайков фильма
     */
    Map<Long, Set<User>> getLikeIds();

}

