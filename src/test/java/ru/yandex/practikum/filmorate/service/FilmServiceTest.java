package ru.yandex.practikum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practikum.filmorate.exception.FilmValidationException;
import ru.yandex.practikum.filmorate.exception.NotFoundException;
import ru.yandex.practikum.filmorate.exception.UserValidationException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practikum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    FilmService filmService = new FilmService(new InMemoryFilmStorage(new InMemoryUserStorage()), new InMemoryUserStorage());
    Film film1 = Film.builder()
            .name("Матрица")
            .description("Нео крут!")
            .releaseDate(LocalDate.of(2009, 12, 10))
            .duration(78)
            .build();

    Film filmLongDescription = Film.builder()
            .name("Матриса")
            .description("Нео крут врпваролврп лворвлпр  флпрлпрлопр лфлпфрвпл рл лфрпл рлрлрфп лршгенкшенуш  е  плп" +
                    "орлплпрлапраолпрлаорплаопрлапгапрарпрп рплрапопгнекген миьсимьсимпа папаопвоа енегеше га" +
                    "гнкшгепваофыгсир рпцьлдцщ трисщц ишшьль мпцсвцсщ лорывлплр лыирлопп црирсвм рорпворпфоп!")
            .releaseDate(LocalDate.of(2008, 12, 10))
            .duration(788)
            .build();

    Film filmEarlyDate = Film.builder()
            .name("Матрица2")
            .description("Нео крут 2!")
            .releaseDate(LocalDate.of(1009, 12, 10))
            .duration(748)
            .build();

    Film filmNegativeDuration = Film.builder()
            .name("Матрица 3")
            .description("Нео крут 23!")
            .releaseDate(LocalDate.of(2009, 12, 10))
            .duration(-748)
            .build();

    @Test
    void addFilmLongDescription() {
        try {
            filmService.addFilmToStorage(filmLongDescription);
        } catch (FilmValidationException e) {
            assertEquals("Описание (Нео крут врпваролврп лворвлпр  флпрлпрлопр лфлпфрвпл рл лфрпл рлрлрфп" +
                    " лршгенкшенуш  е  плпорлплпрлапраолпрлаорплаопрлапгапрарпрп рплрапопгнекген миьсимьсимпа папаопвоа" +
                    " енегеше гагнкшгепваофыгсир рпцьлдцщ трисщц ишшьль мпцсвцсщ лорывлплр лыирлопп црирсвм рорпворпфоп!)" +
                    " фильма (Матриса) содержит более 200 символов.", e.getMessage());
        }
    }

    @Test
    void addFilmEarlyDate() {
        try {
            filmService.addFilmToStorage(filmEarlyDate);
        } catch (FilmValidationException e) {
            assertEquals("Дата релиза (1009-12-10) фильма (Матрица2) ранее 28 декабря 1895 года.", e.getMessage());
        }
    }

    @Test
    void addNormalFilm() {
        filmService.addFilmToStorage(film1);
        assertEquals(filmService.getFilmStorage().getFilms().get(film1.getId()), film1);
    }

    @Test
    void addNegativeDurationFilm() {
        try {
            filmService.addFilmToStorage(filmNegativeDuration);
        } catch (FilmValidationException e) {
            assertEquals("Продолжительность фильма Матрица 3 отрицательная: -748.", e.getMessage());
        }
    }

    @Test
    void getAllFilmsFromStorage() {
    }

    @Test
    void getFilmById() {
    }

    @Test
    void addLikeToFilm() {
    }

    @Test
    void removeLikeFromFilm() {
    }

    @Test
    void getPopularOrTenFirstFilms() {
    }
}