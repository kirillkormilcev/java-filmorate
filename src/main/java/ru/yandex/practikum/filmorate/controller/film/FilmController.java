package ru.yandex.practikum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.controller.AbstractController;
import ru.yandex.practikum.filmorate.model.film.Film;

import java.time.LocalDate;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends AbstractController<Film> {

    /** проверка полей добавляемого/обновляемого фильма */
    protected boolean dataValidation(Film film) {
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
            if (!storage.containsKey(film.getId())) {
                log.warn("Попытка обновить фильм: '{}' с индексом '{}', отсутствующим в базе.", film.getName(),
                        film.getId());
                throw new FilmValidationException("Фильма с таким индексом нет в базе.");
            }
        }
        for (Film filmAvailable : storage.values()) {
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
