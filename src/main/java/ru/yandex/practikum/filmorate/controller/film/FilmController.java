package ru.yandex.practikum.filmorate.controller.film;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.model.film.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/film")
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (filmValidation(film)) {
            film.setId(FilmId.getId());
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (filmValidation(film)) {
            films.put(film.getId(), film);
        }
        return film;
    }

    /** проверка полей добавляемого/обновляемого фильма */
    private boolean filmValidation(Film film) {
        if (film.getDescription().length() > 200) {
            throw new FilmValidationException("Описание фильма содержит более 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Дата релиза фильма ранее 28 декабря 1895 года.");
        }
        if (film.getDuration() < 0) {
            throw new FilmValidationException("Продолжительность фильма отрицательная.");
        }
        if (film.getId() != 0) {
            if (!films.containsKey(film.getId())) {
                throw new FilmValidationException("Фильма с таким индексом нет в базе.");
            }
        }
        for (Film filmAvailable : films.values()) {
            if (film.getName().equals(filmAvailable.getName())) {
                if (filmAvailable.getId() == film.getId()) {
                    return true;
                } else {
                    throw new FilmValidationException("Фильм с таким названием уже есть в базе.");
                }
            }
        }
        return true;
    }
}
