package ru.yandex.practikum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practikum.filmorate.exception.*;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.Genre;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.storage.FilmStorage;
import ru.yandex.practikum.filmorate.storage.LikeStorage;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Getter //для Junit тестов
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(@Qualifier FilmStorage filmStorage, @Qualifier UserStorage userStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
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
        return filmStorage.getFilmById(filmId);
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
        likeStorage.updateFilmLikesRating(film.getId());
        return film;
    }

    /**
     * поставить лайк фильму
     */
    public Film addLikeToFilm(long filmId, long userId) {
        checkFilmId(filmId);
        if (!userStorage.getAllUserIds().contains(userId)) {
            throw new NotFoundException("Пользователя с индексом: " + userId + " нет в базе пользователей.");
        }
        likeStorage.addLikeUserToFilm(filmId, userId); /* добавить лайкнувшего пользователя к фильму */
        return filmStorage.getFilmById(filmId);
    }

    /**
     * удалить лайк у фильма
     */
    public Film removeLikeFromFilm(long filmId, long userId) {
        checkFilmId(filmId);
        if (!userStorage.getAllUserIds().contains(userId)) {
            throw new NotFoundException("Пользователя с индексом: " + userId + " нет в базе пользователей.");
        }
        likeStorage.removeLikeUserFromFilm(filmId, userId); /* удалить лайкнувшего пользователя у фильма */
        return filmStorage.getFilmById(filmId);
    }

    /**
     * популярные либо первые десять фильмов
     */
    public List<Film> getPopularOrTenFirstFilms(Integer count) {
        if (count < 0) {
            throw new IncorrectRequestParamException("Передано отрицательное количество лайков у фильма");
        } else {
            final Comparator<Film> comparatorSortByLikeCount = (o1, o2) -> { /* компаратор сортировки по количеству лайков */
                if (o2.getLikesRating() > o1.getLikesRating()) {
                    return 1;
                } else if (o2.getLikesRating() < o1.getLikesRating()) {
                    return -1;
                } else {
                    return (int) (o1.getId() - o2.getId()); /* при равных лайках по id */
                }
            };
            return filmStorage.getListOfFilms().stream().sorted(comparatorSortByLikeCount).limit(count).collect(Collectors.toList());
        }
    }

    /**
     * список всех жанров
     */
    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    /**
     * жанр по id
     */
    public Genre getGenreById(int id) {
        if (id <= 0) {
            throw new GenreValidationException("Значение индекса жанра передано меньше или равно 0.");
        }
        return filmStorage.getGenreById(id);
    }

    /**
     * список всех MPA рейтингов
     */
    public List<MPA> getAllMPAs() {
        return filmStorage.getAllMPAs();
    }

    /**
     * MPA рейтинг по id
     */
    public MPA getMPAById(int id) {
        if (id <= 0) {
            throw new MPAValidationException("Значение индекса рейтинга MPA передано меньше или равно 0.");
        }
        return filmStorage.getMPAById(id);
    }

    /**
     * проверка полей фильма
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
        for (Film filmAvailable : filmStorage.getListOfFilms()) {
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
        if (!filmStorage.getAllFilmIds().contains(filmId)) {
            throw new NotFoundException("Фильма с индексом: " + filmId + " нет в базе фильмов.");
        }
    }
}
