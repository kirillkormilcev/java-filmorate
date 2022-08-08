package ru.yandex.practikum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.model.user.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceLikesTest {

    private final FilmService filmService1;
    private final UserService userService1;

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
    void addAndRemoveLikeToFilmAndGetPopular() {
        filmService1.addFilmToStorage(film1);
        filmService1.addFilmToStorage(film2);
        filmService1.addFilmToStorage(film3);
        filmService1.addFilmToStorage(film4);
        filmService1.addFilmToStorage(film5);

        userService1.addUserToStorage(user1);
        userService1.addUserToStorage(user2);
        userService1.addUserToStorage(user3);

        filmService1.addLikeToFilm(1, 1);
        filmService1.addLikeToFilm(4, 1);
        filmService1.addLikeToFilm(4, 2);
        filmService1.addLikeToFilm(4, 3);

        filmService1.addLikeToFilm(3, 2);
        filmService1.addLikeToFilm(3, 3);
        filmService1.addLikeToFilm(5, 2);

        assertEquals(1, filmService1.getFilmById(1L).getLikesRating());
        assertEquals(0, filmService1.getFilmById(2L).getLikesRating());
        assertEquals(2, filmService1.getFilmById(3L).getLikesRating());
        assertEquals(3, filmService1.getFilmById(4L).getLikesRating());
        assertEquals(1, filmService1.getFilmById(5L).getLikesRating());

        assertEquals(film4, filmService1.getPopularOrTenFirstFilms(5).get(0));
        assertEquals(film3, filmService1.getPopularOrTenFirstFilms(5).get(1));
        assertEquals(film1, filmService1.getPopularOrTenFirstFilms(5).get(2));
        assertEquals(film5, filmService1.getPopularOrTenFirstFilms(5).get(3));
        assertEquals(film2, filmService1.getPopularOrTenFirstFilms(5).get(4));

        //List<Film> user1FilmsActual = new ArrayList<>(userService1.getUserStorage().getLikedFilmIds().get(1L));
        //List<Film> user1FilmsExpected = new ArrayList<>(List.of(film1, film4));

        //assertEquals(user1FilmsExpected, user1FilmsActual);

        filmService1.removeLikeFromFilm(3, 2);
        filmService1.removeLikeFromFilm(3, 3);

        assertEquals(1, filmService1.getFilmById(1L).getLikesRating());
        assertEquals(0, filmService1.getFilmById(2L).getLikesRating());
        assertEquals(0, filmService1.getFilmById(3L).getLikesRating());
        assertEquals(3, filmService1.getFilmById(4L).getLikesRating());
        assertEquals(1, filmService1.getFilmById(5L).getLikesRating());

        assertEquals(film4, filmService1.getPopularOrTenFirstFilms(5).get(0));
        assertEquals(film1, filmService1.getPopularOrTenFirstFilms(5).get(1));
        assertEquals(film5, filmService1.getPopularOrTenFirstFilms(5).get(2));
        assertEquals(film2, filmService1.getPopularOrTenFirstFilms(5).get(3));
        assertEquals(film3, filmService1.getPopularOrTenFirstFilms(5).get(4));
    }
}