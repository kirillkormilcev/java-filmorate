package ru.yandex.practikum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController (FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return filmService.getAllUsersFromStorage();
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilmToStorage(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateData(@Valid @RequestBody Film film) {
        return filmService.updateFilmInStorage(film);
    }
}
