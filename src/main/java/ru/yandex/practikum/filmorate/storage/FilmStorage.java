package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

public interface FilmStorage {
    Map<Long, Film> filmMap = new LinkedHashMap<>();
    Map<Long, Set<User>> likeIdsMap = new HashMap<>();
    IdGenerator idGenerator = new IdGenerator();

    /** список всех фильмов */
    List<Film> getListOfFilms();

    /** добавить фильм */
    Film addFilm(Film film);

    /** обновить фильм*/
    Film updateFilm(Film film);

    /** добавить лайк */
    void addLikeUserToFilm(long filmId, long userId);

    /** удалить лайк */
    void removeLikeUserFromFilm(long filmId, long userId);

    /** геттер мапы фильмов*/
    Map<Long, Film> getFilmMap();

    /** геттер лайков фильма*/
    Map<Long, Set<User>> getLikeIdsMap();
}

