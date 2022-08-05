package ru.yandex.practikum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practikum.filmorate.exception.FilmValidationException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practikum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    UserStorage userStorage = new InMemoryUserStorage();
    FilmService filmService = new FilmService(new InMemoryFilmStorage(userStorage), userStorage);
    UserService userService = new UserService(userStorage);
    Film film1 = Film.builder()
            .name("Матрица")
            .description("Нео крут!")
            .releaseDate(LocalDate.of(2009, 12, 10))
            .duration(78)
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

    Film filmExist1 = Film.builder()
            .name("Матрица 4")
            .description("Нео крут 234!")
            .releaseDate(LocalDate.of(2008, 12, 10))
            .duration(80)
            .build();

    Film filmExist2 = Film.builder()
            .name("Матрица 4")
            .description("Нео крут 23446!")
            .releaseDate(LocalDate.of(2007, 12, 10))
            .duration(80)
            .build();

    Film film2 = Film.builder()
            .name("Матрица 5")
            .description("Нео крут5!")
            .releaseDate(LocalDate.of(2005, 12, 10))
            .duration(72)
            .build();

    Film film3 = Film.builder()
            .name("Матрица 6")
            .description("Нео крут5!")
            .releaseDate(LocalDate.of(2005, 12, 10))
            .duration(72)
            .build();

    Film film4 = Film.builder()
            .name("Матрица 7")
            .description("Нео крут7!")
            .releaseDate(LocalDate.of(2003, 12, 10))
            .duration(727)
            .build();

    Film film5 = Film.builder()
            .name("Матрица 8")
            .description("Нео крут8!")
            .releaseDate(LocalDate.of(2002, 12, 10))
            .duration(77)
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
        assertEquals(filmService.getFilmStorage().getFilms().size(), filmService.getAllFilmsFromStorage().size());
    }

    @Test
    void getFilmById() {
        filmService.addFilmToStorage(film5);
        assertEquals(filmService.getFilmStorage().getFilms().get(film5.getId()), filmService.getFilmById(film5.getId()));
    }

    @Test
    void addAndRemoveLikeToFilmAndGetPopular() {
        filmService.getFilmStorage().getFilms().clear();
        filmService.getFilmStorage().getLikeIds().clear();

        filmService.addFilmToStorage(film1);
        filmService.addFilmToStorage(film2);
        filmService.addFilmToStorage(film3);
        filmService.addFilmToStorage(film4);
        filmService.addFilmToStorage(film5);

        userService.addUserToStorage(user1);
        userService.addUserToStorage(user2);
        userService.addUserToStorage(user3);

        filmService.addLikeToFilm(1, 1);
        filmService.addLikeToFilm(4, 1);
        filmService.addLikeToFilm(4, 2);
        filmService.addLikeToFilm(4, 3);

        filmService.addLikeToFilm(3, 2);
        filmService.addLikeToFilm(3, 3);
        filmService.addLikeToFilm(5, 2);

        assertEquals(1, filmService.getFilmById(1L).getLikesRating());
        assertEquals(0, filmService.getFilmById(2L).getLikesRating());
        assertEquals(2, filmService.getFilmById(3L).getLikesRating());
        assertEquals(3, filmService.getFilmById(4L).getLikesRating());
        assertEquals(1, filmService.getFilmById(5L).getLikesRating());

        assertEquals(film4, filmService.getPopularOrTenFirstFilms(5).get(0));
        assertEquals(film3, filmService.getPopularOrTenFirstFilms(5).get(1));
        assertEquals(film1, filmService.getPopularOrTenFirstFilms(5).get(2));
        assertEquals(film5, filmService.getPopularOrTenFirstFilms(5).get(3));
        assertEquals(film2, filmService.getPopularOrTenFirstFilms(5).get(4));

        List<Film> user1FilmsActual = new ArrayList<>(userService.getUserStorage().getLikedFilmIds().get(1L));
        List<Film> user1FilmsExpected = new ArrayList<>(List.of(film1, film4));

        assertEquals(user1FilmsExpected, user1FilmsActual);

        filmService.removeLikeFromFilm(3, 2);
        filmService.removeLikeFromFilm(3, 3);

        assertEquals(1, filmService.getFilmById(1L).getLikesRating());
        assertEquals(0, filmService.getFilmById(2L).getLikesRating());
        assertEquals(0, filmService.getFilmById(3L).getLikesRating());
        assertEquals(3, filmService.getFilmById(4L).getLikesRating());
        assertEquals(1, filmService.getFilmById(5L).getLikesRating());

        assertEquals(film4, filmService.getPopularOrTenFirstFilms(5).get(0));
        assertEquals(film1, filmService.getPopularOrTenFirstFilms(5).get(1));
        assertEquals(film5, filmService.getPopularOrTenFirstFilms(5).get(2));
        assertEquals(film2, filmService.getPopularOrTenFirstFilms(5).get(3));
        assertEquals(film3, filmService.getPopularOrTenFirstFilms(5).get(4));
    }
}