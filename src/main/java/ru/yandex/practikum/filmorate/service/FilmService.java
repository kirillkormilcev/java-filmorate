package ru.yandex.practikum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practikum.filmorate.exception.FilmValidationException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public ResponseEntity<List<Film>> getAllUsersFromStorage() {
        if (filmStorage.getFilms().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(filmStorage.getListOfFilms(), HttpStatus.OK);
        }
    }

    public ResponseEntity<Film> addFilmToStorage (Film film) {
        if (filmValidation(film)) {
            filmStorage.addFilm(film);
            return new ResponseEntity<>(film, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); /* TODO вернуть разные коды в зависимости от ситуации*/
        }
    }

    public ResponseEntity<Film> updateFilmInStorage(Film film) {
        if (filmValidation(film)) {
            filmStorage.updateFilm(film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); /* TODO вернуть разные коды в зависимости от ситуации*/
        }
    }

    private boolean filmValidation(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Попытка добавить или обновить фильм: '{}' с описанием более 200 символов:\n'{}'.",
                    film.getName(), film.getDescription());
            throw new FilmValidationException("Описание фильма содержит более 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка добавить или обновить фильм: '{}' с датой релиза ранее 1895.12.28: '{}'.",
                    film.getName(), film.getReleaseDate());
            throw new FilmValidationException("Дата релиза фильма ранее 28 декабря 1895 года.");
        }
        if (film.getDuration() < 0) {
            log.warn("Попытка добавить или обновить фильм: '{}' с отрицательной длительностью: '{}'.",
                    film.getName(), film.getDuration());
            throw new FilmValidationException("Продолжительность фильма отрицательная.");
        }
        if (film.getId() != 0) {
            if (!filmStorage.getFilms().containsKey(film.getId())) {
                log.warn("Попытка обновить фильм: '{}' с индексом '{}', отсутствующим в базе.", film.getName(),
                        film.getId());
                throw new FilmValidationException("Фильма с таким индексом нет в базе.");
            }
        }
        for (Film filmAvailable : filmStorage.getFilms().values()) {
            if (film.getName().equals(filmAvailable.getName())) {
                if (filmAvailable.getId() == film.getId()) {
                    return true;
                } else {
                    log.warn("Попытка обновить или добавить фильм: '{}', который уже есть в базе (id: '{}').",
                            film.getName(), filmAvailable.getId());
                    throw new FilmValidationException("Фильм с таким названием уже есть в базе.");
                }
            }
        }
        return true;
    }
}
