package ru.yandex.practikum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practikum.filmorate.exception.FilmValidationException;
import ru.yandex.practikum.filmorate.exception.IncorrectRequestParamException;
import ru.yandex.practikum.filmorate.exception.NotFoundException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.storage.FilmStorage;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * список всех фильмов в хранилище
     */
    public List<Film> getAllFilmsFromStorage() {
        return filmStorage.getListOfFilms();
    }

    /**
     * фильм по Id
     */
    public Film getFilmById(Long filmId) {
        checkFilmId(filmId);
        return filmStorage.getFilmMap().get(filmId);
    }

    /**
     * добавить фильм в хранилище
     */
    public Film addFilmToStorage(Film film) {
        filmValidation(film);
        filmStorage.addFilm(film);
        filmStorage.addOrRemoveFilmToSortedByLikesSet(film);
        return film;
    }

    /**
     * обновить фильм в хранилище
     */
    public Film updateFilmInStorage(Film film) {
        checkFilmId(film.getId());
        filmValidation(film);
        filmStorage.updateFilm(film);
        filmStorage.addOrRemoveFilmToSortedByLikesSet(film);
        return film;
    }

    /**
     * поставить лайк фильму
     */
    public Film addLikeToFilm(long filmId, long userId) {
        checkFilmId(filmId);
        checkFilmId(userId);
        filmStorage.addLikeUserToFilm(filmId, userId); /* добавить лайкнувшего пользователя к фильму */
        if (!userStorage.getLikedFilmIdsMap().containsKey(userId)) {
            userStorage.getLikedFilmIdsMap().put(userId, new HashSet<>());
        }
        userStorage.getLikedFilmIdsMap().get(userId).add(filmStorage.getFilmMap().get(filmId));
        /* добавить понравившийся фильм пользователю */
        getFilmById(filmId).setLikesCount(filmStorage.getLikeIdsMap().get(filmId).size());
        /* обновить количество лайков у фильма */
        filmStorage.addOrRemoveFilmToSortedByLikesSet(filmStorage.getFilmMap().get(filmId));
        /* обновить фильм в сортированном множестве */
        return filmStorage.getFilmMap().get(filmId);
    }

    /**
     * удалить лайк у фильма
     */
    public Film removeLikeFromFilm(long filmId, long userId) {
        checkFilmId(filmId);
        checkFilmId(userId);
        filmStorage.removeLikeUserFromFilm(filmId, userId); /* удалить лайкнувшего пользователя у фильма */
        userStorage.getLikedFilmIdsMap().get(userId).remove(filmStorage.getFilmMap().get(filmId));
        /* удалить понравившийся фильм у пользователя */
        getFilmById(filmId).setLikesCount(filmStorage.getLikeIdsMap().get(filmId).size());
        /* обновить количество лайков у фильма */
        filmStorage.addOrRemoveFilmToSortedByLikesSet(filmStorage.getFilmMap().get(filmId));
        /* обновить фильм в сортированном множестве */
        return filmStorage.getFilmMap().get(filmId);
    }

    /**
     * популярные либо первые десять фильмов
     */
    public List<Film> getPopularOrTenFirstFilms(Integer count) {
        if (count == null) {
            return getTenFirstFilms();
        }
        if (count < 0) {
            throw new IncorrectRequestParamException("Передано отрицательное количество лайков у фильма");
        }
        if (count != 0) {
            return getMostPopularFilmsByCount(count);
        } else {
            return getTenFirstFilms();
        }
    }

    /**
     * фильмы с наибольшим количеством лайков по указанному количеству
     */
    private List<Film> getMostPopularFilmsByCount(Integer count) {
        return filmStorage.getSortedByLikeCountFilmSet().stream().limit(count).collect(Collectors.toList());
    }

    /**
     * десять первых фильмов
     */
    private List<Film> getTenFirstFilms() {
        return filmStorage.getFilmMap().values().stream().limit(10).collect(Collectors.toList());
    }

    /**
     * проверка фильма
     */
    private void filmValidation(Film film) {
        if (film.getDescription().length() > 200) {
            throw new FilmValidationException("Описание (" + film.getDescription() + ") фильма (" + film.getName()
                    + ") содержит более 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Дата релиза (" + film.getReleaseDate() + ") фильма (" + film.getName()
                    + ") ранее 28 декабря 1895 года.");
        }
        if (film.getDuration() < 0) {
            throw new FilmValidationException("Продолжительность фильма " + film.getName() + " отрицательная: "
                    + film.getDuration() + ".");
        }
        for (Film filmAvailable : filmStorage.getFilmMap().values()) {
            if (film.getName().equals(filmAvailable.getName())) {
                if (filmAvailable.getId() == film.getId()) {
                    return;
                } else {
                    throw new FilmValidationException("Фильм " + film.getName() + " с таким названием уже есть в базе " +
                            "под индексом: " + filmAvailable.getId() + ".");
                }
            }
        }
    }

    /**
     * проверка наличия индекса фильма в базе
     */
    private void checkFilmId(long filmId) {
        if (!filmStorage.getFilmMap().containsKey(filmId)) {
            throw new NotFoundException("Фильма с индексом: " + filmId + " нет в базе фильмов.");
        }
    }
}
