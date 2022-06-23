package ru.yandex.practikum.filmorate.controller.film;

import org.junit.jupiter.api.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practikum.filmorate.FilmorateApplication;
import ru.yandex.practikum.filmorate.model.film.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController filmController = new FilmController();
    Film film = Film.builder()
            .name("Матрица")
            .description("Нео крут!")
            .releaseDate(LocalDate.of(2009, 12, 10))
            .duration(78)
            .build();

    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    RestTemplate restTemplate = restTemplateBuilder.build();
    String url = "http://localhost:8080/films";

    @Test
    @DisplayName("Добавление фильма с пустым названием")
    void addEmptyNameFilm() {
        Film film1 = Film.builder()
                .name("")
                .description("Нео крут!")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(78)
                .build();
        HttpEntity<Film> entity = new HttpEntity<>(film1);
        try {
            ResponseEntity<Film> response = restTemplate.postForEntity(url, entity, Film.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Неожиданный статус ответа");
        }
    }

    @Test
    @DisplayName("Обновление фильма с null названием")
    void updateNullNameFilm() {
        filmController.getFilms().clear();
        filmController.addFilm(film);
        Film film1 = Film.builder()
                .id(film.getId())
                .name(null)
                .description("Нео крут!")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(78)
                .build();
        HttpEntity<Film> entity1 = new HttpEntity<>(film1);
        try {
            ResponseEntity<Film> response = restTemplate.exchange(url, HttpMethod.PUT, entity1, Film.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Неожиданный статус ответа");
        }
    }

    @Test
    @DisplayName("Добавление фильма с существующим названием")
    void addExistingNameFilm() {
        filmController.addFilm(film);
        Film film1 = Film.builder()
                .name("Матрица")
                .description("Нео крут!")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(78)
                .build();
        try {
            filmController.updateFilm(film1);
        } catch (FilmValidationException e) {
            assertEquals(FilmValidationException.class, e.getClass(), "При добавлении фильма не выброшено исключение.");
            assertEquals("Фильм с таким названием уже есть в базе.", e.getMessage(),
                    "Не верное сообщение в исключении");
        }
    }

    @Test
    @DisplayName("Добавление фильма с длинным описанием")
    void addLongDescriptionFilm() {
        Film film = Film.builder()
                .name("")
                .description("Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! " +
                        "Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! " +
                        "Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! " +
                        "Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! " +
                        "Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут! Нео крут!")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(78)
                .build();
        try {
            filmController.addFilm(film);
        } catch (FilmValidationException e) {
            assertEquals(FilmValidationException.class, e.getClass(), "При добавлении фильма не выброшено исключение.");
            assertEquals("Описание фильма содержит более 200 символов.", e.getMessage(),
                    "Не верное сообщение в исключении");
        }
    }

    @Test
    @DisplayName("Добавление фильма с ранней датой релиза")
    void addEarlyReleaseDateFilm() {
        Film film = Film.builder()
                .name("Матрица")
                .description("Нео крут!")
                .releaseDate(LocalDate.of(1458, 12, 10))
                .duration(78)
                .build();
        try {
            filmController.addFilm(film);
        } catch (FilmValidationException e) {
            assertEquals(FilmValidationException.class, e.getClass(), "При добавлении фильма не выброшено исключение.");
            assertEquals("Дата релиза фильма ранее 28 декабря 1895 года.", e.getMessage(),
                    "Не верное сообщение в исключении");
        }
    }

    @Test
    @DisplayName("Добавление фильма с отрицательной длительностью")
    void addNegativeDurationFilm() {
        Film film = Film.builder()
                .name("Матрица")
                .description("Нео крут!")
                .releaseDate(LocalDate.of(1958, 12, 10))
                .duration(-78)
                .build();
        try {
            filmController.addFilm(film);
        } catch (FilmValidationException e) {
            assertEquals(FilmValidationException.class, e.getClass(), "При добавлении фильма не выброшено исключение.");
            assertEquals("Продолжительность фильма отрицательная.", e.getMessage(),
                    "Не верное сообщение в исключении");
        }
    }

    @Test
    @DisplayName("Обновление фильма с несуществующим id")
    void updateNotExistingIdFilm() {
        filmController.addFilm(film);
        Film film1 = Film.builder()
                .id(10)
                .name("Матрица обновлена")
                .description("Нео крут!")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(78)
                .build();
        try {
            filmController.addFilm(film1);
        } catch (FilmValidationException e) {
            assertEquals(FilmValidationException.class, e.getClass(), "При добавлении фильма не выброшено исключение.");
            assertEquals("Фильма с таким индексом нет в базе.", e.getMessage(),
                    "Не верное сообщение в исключении");
        }
    }
}