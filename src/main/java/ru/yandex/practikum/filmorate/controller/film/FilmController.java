package ru.yandex.practikum.filmorate.controller.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.Genre;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Обработка эндпойнта GET /films");
        return new ResponseEntity<>(filmService.getAllFilmsFromStorage(), HttpStatus.OK);
    }

    @GetMapping("/films/{filmId}")
    public ResponseEntity<Film> getFilmById(@PathVariable long filmId) {
        log.info("Обработка эндпойнта GET /films/{}", filmId);
        return new ResponseEntity<>(filmService.getFilmById(filmId), HttpStatus.OK);
    }

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Обработка эндпойнта POST /films");
        return new ResponseEntity<>(filmService.addFilmToStorage(film), HttpStatus.CREATED);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateData(@Valid @RequestBody Film film) {
        log.info("Обработка эндпойнта PUT /films");
        return new ResponseEntity<>(filmService.updateFilmInStorage(film), HttpStatus.OK);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> putLikeToFilm(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Обработка эндпойнта PUT /films/{}/like/{}", filmId, userId);
        return new ResponseEntity<>(filmService.addLikeToFilm(filmId, userId), HttpStatus.OK);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> deleteLikeFromFilm(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Обработка эндпойнта DELETE /films/{}/like/{}", filmId, userId);
        return new ResponseEntity<>(filmService.removeLikeFromFilm(filmId, userId), HttpStatus.OK);
    }

    @GetMapping("/films/popular")
    public ResponseEntity<List<Film>> getPopularByCountOrFirstTenFilms(@RequestParam(required = false,
            defaultValue = "10") Integer count) {
        log.info("Обработка эндпойнта GET /films/popular{}", "?count=" + count);
        return new ResponseEntity<>(filmService.getPopularOrTenFirstFilms(count), HttpStatus.OK);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        log.info("Обработка эндпойнта GET /genres");
        return new ResponseEntity<>(filmService.getAllGenres(), HttpStatus.OK);
    }

    @GetMapping("/genres/{genreId}")
    public ResponseEntity<Genre> getAllGenres(@PathVariable int genreId) {
        log.info("Обработка эндпойнта GET /genres/{}", genreId);
        return new ResponseEntity<>(filmService.getGenreById(genreId), HttpStatus.OK);
    }

    @GetMapping("/mpa")
    public ResponseEntity<List<MPA>> getAllMPAs() {
        log.info("Обработка эндпойнта GET /genres");
        return new ResponseEntity<>(filmService.getAllMPAs(), HttpStatus.OK);
    }

    @GetMapping("/mpa/{MPAId}")
    public ResponseEntity<MPA> getMPAbyId(@PathVariable int MPAId) {
        log.info("Обработка эндпойнта GET /MPA/{}", MPAId);
        return new ResponseEntity<>(filmService.getMPAById(MPAId), HttpStatus.OK);
    }
}
