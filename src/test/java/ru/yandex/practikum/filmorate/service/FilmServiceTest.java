package ru.yandex.practikum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practikum.filmorate.exception.FilmValidationException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.model.user.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {

    private final FilmService filmService;

    MPA mpa = MPA.builder()
            .id(1)
            .build();

    Film film1 = Film.builder()
            .name("Матрица")
            .description("Нео крут!")
            .releaseDate(LocalDate.of(2009, 12, 10))
            .duration(78)
            .MPA(mpa)
            .build();

    String description = "Надо уважать всякого человека, какой бы он ни был жалкий и смешной. Надо помнить," +
            " что во всяком человеке живёт тот же дух, какой и в нас. Даже тогда, когда человек отвратителен" +
            " и душой и телом, надо думать так: “Да, на свете должны быть и такие уроды, и надо терпеть их”." +
            " Если же мы показываем таким людям наше отвращение, то, во-первых, мы несправедливы, а во-вторых," +
            " вызываем таких людей на войну не на жизнь, а на смерть.";
    Film filmLongDescription = Film.builder()
            .name("Матрона")
            .description(description)
            .releaseDate(LocalDate.of(2008, 12, 10))
            .duration(788)
            .MPA(mpa)
            .build();

    Film filmEarlyDate = Film.builder()
            .name("Матрица2")
            .description("Нео крут 2!")
            .releaseDate(LocalDate.of(1009, 12, 10))
            .duration(748)
            .MPA(mpa)
            .build();

    Film filmNegativeDuration = Film.builder()
            .name("Матрица 3")
            .description("Нео крут 23!")
            .releaseDate(LocalDate.of(2009, 12, 10))
            .duration(-748)
            .MPA(mpa)
            .build();

    Film filmExist1 = Film.builder()
            .name("Матрица 4")
            .description("Нео крут 234!")
            .releaseDate(LocalDate.of(2008, 12, 10))
            .duration(80)
            .MPA(mpa)
            .build();

    Film filmExist2 = Film.builder()
            .name("Матрица 4")
            .description("Нео крут 23446!")
            .releaseDate(LocalDate.of(2007, 12, 10))
            .duration(80)
            .MPA(mpa)
            .build();

    Film film2 = Film.builder()
            .name("Матрица 5")
            .description("Нео крут5!")
            .releaseDate(LocalDate.of(2005, 12, 10))
            .duration(72)
            .MPA(mpa)
            .build();

    Film film3 = Film.builder()
            .name("Матрица 6")
            .description("Нео крут5!")
            .releaseDate(LocalDate.of(2005, 12, 10))
            .duration(72)
            .MPA(mpa)
            .build();

    Film film4 = Film.builder()
            .name("Матрица 7")
            .description("Нео крут7!")
            .releaseDate(LocalDate.of(2003, 12, 10))
            .duration(727)
            .MPA(mpa)
            .build();

    Film film5 = Film.builder()
            .name("Матрица 8")
            .description("Нео крут8!")
            .releaseDate(LocalDate.of(2002, 12, 10))
            .duration(77)
            .MPA(mpa)
            .build();

    User user1 = User.builder()
            .email("kirill@kormilcev.ru")
            .login("kirill")
            .name("Кирилл")
            .birthday(LocalDate.of(1982,2,4))
            .build();
    User user2 = User.builder()
            .email("jkgjg@ssh.ru")
            .login("hermitage")
            .name("Пополам")
            .birthday(LocalDate.of(1981,2,4))
            .build();
    User user3 = User.builder()
            .email("jfdhgkgjg@ssh.ru")
            .login("hello")
            .name("Половинка")
            .birthday(LocalDate.of(1980,2,4))
            .build();

    @Test
    void addFilmLongDescription() {
        try {
            filmService.addFilmToStorage(filmLongDescription);
        } catch (FilmValidationException e) {
            assertEquals("Описание (" + description + ") фильма (Матрона) содержит более 200 символов.",
                    e.getMessage());
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
        assertEquals(filmService.getFilmStorage().getFilmById(film1.getId()), film1);
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
    void addExistFilm() {
        filmService.addFilmToStorage(filmExist1);
        try {
            filmService.addFilmToStorage(filmExist2);
        } catch (FilmValidationException e) {
            assertEquals("Фильм Матрица 4 с таким названием уже есть в базе под индексом: 1.", e.getMessage());
        }
    }

    @Test
    void updateNormalFilm() {
        filmService.addFilmToStorage(film2);
        film3.setId(film2.getId());
        filmService.updateFilmInStorage(film3);
        assertEquals(filmService.getFilmById(film3.getId()), film3);
    }

    @Test
    void getAllFilmsFromStorage() {
        filmService.addFilmToStorage(film4);
        assertEquals(filmService.getFilmStorage().getListOfFilms().size(), filmService.getAllFilmsFromStorage().size());
    }

    @Test
    void getFilmById() {
        filmService.addFilmToStorage(film5);
        assertEquals(filmService.getFilmStorage().getFilmById(film5.getId()), filmService.getFilmById(film5.getId()));
    }
}