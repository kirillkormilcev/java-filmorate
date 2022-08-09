package ru.yandex.practikum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.impl.DBFilmStorage;
import ru.yandex.practikum.filmorate.storage.impl.DBLikeStorage;
import ru.yandex.practikum.filmorate.storage.impl.DBUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final DBUserStorage userStorage;
    private final DBFilmStorage filmStorage;
    private final DBLikeStorage likeStorage;

    User user1 = User.builder()
            .email("kirill@kormilcev.ru")
            .login("kirill")
            .name("Кирилл")
            .birthday(LocalDate.of(1982,2,4))
            .build();
    User user2 = User.builder()
            .email("jkgjg@ssh.ru")
            .login("hello")
            .name("Половинка")
            .birthday(LocalDate.of(1981,2,4))
            .build();

    User user3 = User.builder()
            .email("jfdhgkgjg@ssh.ru")
            .login("hellgho")
            .name("Половинка")
            .birthday(LocalDate.of(1980,2,4))
            .build();

    User user4 = User.builder()
            .email("jfdsdfghhgkgjg@ssh.ru")
            .login("hellodfh")
            .name("Половинкаавра")
            .birthday(LocalDate.of(1980,2,4))
            .build();

    User user5 = User.builder()
            .email("jfdgfddsdfghhgkgjg@ssh.ru")
            .login("hellosdfdfh")
            .name("Половинквапаавра")
            .birthday(LocalDate.of(1980,2,4))
            .build();

    User user6 = User.builder()
            .email("jfdgfddsdsadffghhgkgjg@ssh.ru")
            .login("hellosdafsdfdfh")
            .name("Половинкпавапаавра")
            .birthday(LocalDate.of(1980,2,4))
            .build();

    User user7 = User.builder()
            .email("jfdgfddhhsdhsdfghhgkgjg@ssh.ru")
            .login("hellossdhfghddfdfh")
            .name("Половинвппквапаавра")
            .birthday(LocalDate.of(1980,2,4))
            .build();

    User userWithSpacesInLogin = User.builder()
            .email("jkgjg@ssfdgsh.ru")
            .login("hello gay")
            .name("Половник")
            .birthday(LocalDate.of(1980,2,4))
            .build();
    User userEmptyName = User.builder()
            .email("jkdfgjg@ssfdgsh.ru")
            .login("Hermitage")
            .name(" ")
            .birthday(LocalDate.of(1979,2,4))
            .build();

    User userFutureBirthdate = User.builder()
            .email("jkdfjhgjg@ssfdgsh.ru")
            .login("Sorbonne")
            .name("Главная рыба")
            .birthday(LocalDate.of(1079,2,4))
            .build();

    User userWithExistEmail1 = User.builder()
            .email("hggjgkjmn@ioyure.ru")
            .login("Glock")
            .name("Лолита")
            .birthday(LocalDate.of(1962,2,4))
            .build();

    User userWithExistEmail2 = User.builder()
            .email("hgasdfgdfgjgkjmn@ioyure.ru")
            .login("response")
            .name("Лолита Милявская")
            .birthday(LocalDate.of(1942,2,4))
            .build();

    User userWithExistEmail3 = User.builder()
            .email("hggjgkjmn@ioyure.ru")
            .login("entity")
            .name("Валидол")
            .birthday(LocalDate.of(1952,2,4))
            .build();
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

    @Test
    void users() {
        userStorage.addUser(user1); // id=1 или 3
        assertEquals(userStorage.getUserById(user1.getId()).getId(), user1.getId(),
                "Нормальный пользователь, ожидается id = 1 или 3.");
        assertEquals(1 | 3, userStorage.getListOfUsers().size(), "После добавления 1 " +
                "пользователя ожидается список пользователей размером 1 или 3.");
        assertEquals(user1, userStorage.getUserById(user1.getId()), "Ожидается что пользователь добавляемый" +
                " и пользователь добавленный совпадают.");

        user2.setId(user1.getId());
        userStorage.updateUser(user2); // id=1 или 3
        assertEquals(userStorage.getUserById(user2.getId()).getId(), user2.getId(),
                "Обновление пользователя, ожидается id = 1 или 3.");
        assertEquals(1 | 3, userStorage.getListOfUsers().size(), "После обновления 1 " +
                "пользователя ожидается список пользователей размером 1 или 3.");
        assertEquals(user2, userStorage.getUserById(user2.getId()), "Ожидается что пользователь обновляемый" +
                " и пользователь обновленный совпадают.");

        userStorage.addUser(user3); // id=2 или 4
        userStorage.addUser(user4); // id=3 или 5
        userStorage.addUser(user5); // id=4 или 6
        userStorage.addFriend(user2.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user4.getId());
        userStorage.addFriend(user3.getId(), user4.getId());
        userStorage.addFriend(user5.getId(), user2.getId());
        userStorage.addFriend(user2.getId(), user5.getId());
        userStorage.addFriend(user3.getId(), user5.getId());

        assertEquals(3, userStorage.getUserFriends(user2.getId()).size(),
                "В друзьях у user2 пользователя ожидаются user3, user4, user5");
        assertEquals(2, userStorage.getUserFriends(user3.getId()).size(),
                "В друзьях у user3 пользователя ожидаются user4, user5");
        assertEquals(1, userStorage.getUserFriends(user5.getId()).size(),
                "В друзьях у user5 пользователя ожидается user2");

        userStorage.removeFriend(user3.getId(), user4.getId());

        assertArrayEquals(new User[]{user5}, userStorage.getUserFriends(user3.getId()).toArray(),
                "В друзьях у user3 пользователя ожидается user5");

        assertEquals(4 | 6, userStorage.getAllUserIds().size(),
                "Ожидается пользователей в базе 4 или 6");
    }

    @Test
    void films() {
        filmStorage.addFilm(film1); // id=1
        assertEquals(1, film1.getId(), "Нормальный фильм, ожидается id = 1.");
        assertEquals(1, filmStorage.getListOfFilms().size(), "После добавления 1 " +
                "фильма ожидается список пользователей размером 1.");
        assertEquals(film1, filmStorage.getFilmById(1), "Ожидается что фильм добавляемый" +
                " и фильм добавленный совпадают.");

        film2.setId(film1.getId());
        filmStorage.updateFilm(film2); // id=1
        assertEquals(1, film2.getId(), "Обновление фильма, ожидается id = 1.");
        assertEquals(1, filmStorage.getListOfFilms().size(), "После обновления 1 " +
                "фильма ожидается список фильмов размером 1.");
        assertEquals(film2, filmStorage.getFilmById(1), "Ожидается что фильм обновляемый" +
                " и фильм обновленный совпадают.");

        userStorage.addUser(user6);
        userStorage.addUser(user7);

        likeStorage.addLikeUserToFilm(film2.getId(), user6.getId());
        likeStorage.addLikeUserToFilm(film2.getId(), user7.getId());

        filmStorage.addFilm(film3); // id=2
        likeStorage.addLikeUserToFilm(film3.getId(), user6.getId());

        filmStorage.addFilm(film4); // id=3

        assertEquals(2, filmStorage.getFilmById(film2.getId()).getLikesRating(),
                "У film2 ожидается 2 лайка.");
        assertEquals(1, filmStorage.getFilmById(film3.getId()).getLikesRating(),
                "У film3 ожидается 1 лайк.");
        assertEquals(0, filmStorage.getFilmById(film4.getId()).getLikesRating(),
                "У film4 ожидается 0 лайков.");

        likeStorage.removeLikeUserFromFilm(film2.getId(), user6.getId());
        assertEquals(1, filmStorage.getFilmById(film2.getId()).getLikesRating(),
                "У film2 ожидается 2 лайка.");

        assertArrayEquals(new Long[]{1L, 2L, 3L}, filmStorage.getAllFilmIds().stream().sorted().toArray(),
                "Ожидаются фильмы в базе 1, 2, 3, 4");

        assertEquals("Комедия", filmStorage.getGenreById(1).getName(),
                "Под 1 индексом ожидается Комедия");

        assertEquals(5, filmStorage.getAllMPAs().size(),
                "Список MPA ожидается размером 5.");

        assertEquals("G", filmStorage.getMPAById(1).getName(),
                "Под 1 индексом ожидается G");

        assertEquals(6, filmStorage.getAllGenres().size(),
                "Список жарнов ожидается размером 6.");

    }
}

