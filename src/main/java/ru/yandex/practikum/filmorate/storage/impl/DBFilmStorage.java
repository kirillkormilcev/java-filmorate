package ru.yandex.practikum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Qualifier
public class DBFilmStorage implements FilmStorage {
    @Override
    public List<Film> getListOfFilms() {
        return null;
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public void addLikeUserToFilm(long filmId, long userId) {

    }

    @Override
    public void removeLikeUserFromFilm(long filmId, long userId) {

    }

    @Override
    public Map<Long, Film> getFilms() {
        return null;
    }

    @Override
    public Map<Long, Set<User>> getLikeIds() {
        return null;
    }
}
