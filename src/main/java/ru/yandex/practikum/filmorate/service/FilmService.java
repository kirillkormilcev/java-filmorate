package ru.yandex.practikum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practikum.filmorate.exception.FilmValidationException;
import ru.yandex.practikum.filmorate.exception.IncorrectRequestParamException;
import ru.yandex.practikum.filmorate.exception.NotFoundException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.storage.FilmStorage;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Getter //для Junit тестов
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
     * фильм по id
     */
    public Film getFilmById(Long filmId) {
        checkFilmId(filmId);
        return filmStorage.getFilms().get(filmId);
    }

    /**
     * добавить фильм в хранилище
     */
    public Film addFilmToStorage(Film film) {
        filmValidation(film);
        filmStorage.addFilm(film);
        return film;
    }

    /**
     * обновить фильм в хранилище
     */
    public Film updateFilmInStorage(Film film) {
        checkFilmId(film.getId());
        filmValidation(film);
        filmStorage.updateFilm(film);
        return film;
    }

    /**
     * поставить лайк фильму
     */
    public Film addLikeToFilm(long filmId, long userId) {
        checkFilmId(filmId);
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователя с индексом: " + userId + " нет в базе пользователей.");
        }
        filmStorage.addLikeUserToFilm(filmId, userId); /* добавить лайкнувшего пользователя к фильму */
        if (!userStorage.getLikedFilmIds().containsKey(userId)) {
            userStorage.getLikedFilmIds().put(userId, new HashSet<>());
        } /* если в мапе еще не множества фильмов пролайканых пользователем, то создать */
        userStorage.getLikedFilmIds().get(userId).add(filmStorage.getFilms().get(filmId));
        /* добавить понравившийся фильм пользователю */
        getFilmById(filmId).setLikesCount(filmStorage.getLikeIds().get(filmId).size());
        /* обновить количество лайков у фильма */
        return filmStorage.getFilms().get(filmId);
    }

    /**
     * удалить лайк у фильма
     */
    public Film removeLikeFromFilm(long filmId, long userId) {
        checkFilmId(filmId);
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователя с индексом: " + userId + " нет в базе пользователей.");
        }
        filmStorage.removeLikeUserFromFilm(filmId, userId); /* удалить лайкнувшего пользователя у фильма */
        userStorage.getLikedFilmIds().get(userId).remove(filmStorage.getFilms().get(filmId));
        /* удалить понравившийся фильм у пользователя */
        getFilmById(filmId).setLikesCount(filmStorage.getLikeIds().get(filmId).size());
        /* обновить количество лайков у фильма */
        return filmStorage.getFilms().get(filmId);
    }

    /**
     * популярные либо первые десять фильмов
     */
    public List<Film> getPopularOrTenFirstFilms(Integer count) {
        if (count < 0) {
            throw new IncorrectRequestParamException("Передано отрицательное количество лайков у фильма");
        } else  {
            final Comparator<Film> comparatorSortByLikeCount = (o1, o2) -> { /* компаратор сортировки по количеству лайков */
                 if (o2.getLikesCount() > o1.getLikesCount()) {
                    return 1;
                } else if (o2.getLikesCount() < o1.getLikesCount()) {
                    return -1;
                } else {
                    return (int) (o1.getId() - o2.getId()); /* при равных лайках по id */
                }
            };
            return filmStorage.getFilms().values().stream().sorted(comparatorSortByLikeCount).limit(count).collect(Collectors.toList());
        }
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
        for (Film filmAvailable : filmStorage.getFilms().values()) {
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
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильма с индексом: " + filmId + " нет в базе фильмов.");
        }
    }
}
