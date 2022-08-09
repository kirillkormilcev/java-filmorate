package ru.yandex.practikum.filmorate.storage.impl;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.Genre;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.FilmStorage;
import ru.yandex.practikum.filmorate.storage.IdGenerator;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new LinkedHashMap<>(); /* мапа фильмов */
    private final Map<Long, Set<User>> likeIds = new HashMap<>(); /* мапа множеств лайкнувших пользователей */
    private final IdGenerator idGenerator = new IdGenerator();
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getListOfFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(long id) {
        return null;
    } // заглушка реализации в БД

    @Override
    public Film addFilm(Film film) {
        film.setId(idGenerator.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Long> getAllFilmIds() {
        return null;
    } // заглушка реализации в БД

    /*@Override
    public List<MPA> getAllMPAs() {
        return null;
    }*/ // заглушка реализации в БД

    /*@Override
    public MPA getMPAById(int id) {
        return null;
    } */// заглушка реализации в БД
}
