package ru.yandex.practikum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.FilmStorage;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.time.LocalDate;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

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
            .login("hello")
            .name("Половинка")
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
    void contextLoads() {
    }
}
