package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

public interface FilmStorage {
    Map<Long, Film> filmMap = null;
    SortedSet<Film> sortedByLikeCountFilmSet = null;
    Map<Long, Set<User>> likeIdsMap = null;
    IdGenerator idGenerator = null;

    /**
     * список всех фильмов
     */
    List<Film> getListOfFilms();

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
     * добавить фильм в сортированное по лайкам множество
     */
    void updateFilmInSortedByLikesSet(Film film);

    /**
     * геттер мапы фильмов
     */
    Map<Long, Film> getFilmMap();

    /**
     * геттер лайков фильма
     */
    Map<Long, Set<User>> getLikeIdsMap();

    /**
     * геттер сета сортированных по лайкам фильмов
     */
    SortedSet<Film> getSortedByLikeCountFilmSet();
}

