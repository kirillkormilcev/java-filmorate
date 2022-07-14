package ru.yandex.practikum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Обработка эндпойнта GET /films");
        return new ResponseEntity<>(filmService.getAllFilmsFromStorage(), HttpStatus.OK);
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<Film> getFilmById(@PathVariable long filmId) {
        log.info("Обработка эндпойнта GET /films/{}", filmId);
        return new ResponseEntity<>(filmService.getFilmById(filmId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Обработка эндпойнта POST /films");
        return new ResponseEntity<>(filmService.addFilmToStorage(film), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateData(@Valid @RequestBody Film film) {
        log.info("Обработка эндпойнта PUT /films");
        return new ResponseEntity<>(filmService.updateFilmInStorage(film), HttpStatus.OK);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Film> putLikeToFilm(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Обработка эндпойнта PUT /films/{}/like/{}", filmId, userId);
        return new ResponseEntity<>(filmService.addLikeToFilm(filmId, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Film> deleteLikeFromFilm(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Обработка эндпойнта DELETE /films/{}/like/{}", filmId, userId);
        return new ResponseEntity<>(filmService.removeLikeFromFilm(filmId, userId), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularByCountOrFirstTenFilms(@RequestParam(required = false,
            defaultValue = "10") Integer count) {
        log.info("Обработка эндпойнта GET /films/popular{}", "?count=" + count);
        return new ResponseEntity<>(filmService.getPopularOrTenFirstFilms(count), HttpStatus.OK);
    }
}
