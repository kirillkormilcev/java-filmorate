package ru.yandex.practikum.filmorate.controller.user;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practikum.filmorate.FilmorateApplication;
import ru.yandex.practikum.filmorate.model.user.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController = new UserController();
    User user = User.builder()
            .email("kirill@kormilcev.ru")
            .login("kirill")
            .name("Кирилл")
            .birthday(LocalDate.of(1982,2,4))
            .build();

    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    RestTemplate restTemplate = restTemplateBuilder.build();
    String url = "http://localhost:8080/users";

    @Test
    @DisplayName("Добавление пользователя с пустым email")
    void addEmptyEmailUser() {
        User user = User.builder()
                .email("")
                .login("kirill")
                .name("Кирилл")
                .birthday(LocalDate.of(1982,2,4))
                .build();
        HttpEntity<User> entity = new HttpEntity<>(user);
        try {
            ResponseEntity<User> response = restTemplate.postForEntity(url, entity, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Неожиданный статус ответа");
        }
    }

    @Test
    @DisplayName("Добавление пользователя с null email")
    void addNullEmailUser() {
        User user = User.builder()
                .email(null)
                .login("kirill")
                .name("Кирилл")
                .birthday(LocalDate.of(1982,2,4))
                .build();
        HttpEntity<User> entity = new HttpEntity<>(user);
        try {
            ResponseEntity<User> response = restTemplate.postForEntity(url, entity, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Неожиданный статус ответа");
        }
    }

    @Test
    @DisplayName("Обновление пользователя с не корректным email")
    void updateIncorrectEmailUser() {
        userController.addData(user);
        User user1 = User.builder()
                .id(user.getId())
                .email("hfgkj.uyy@")
                .login("kirill")
                .name("Кирилл")
                .birthday(LocalDate.of(1982,2,4))
                .build();
        HttpEntity<User> entity = new HttpEntity<>(user1);
        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Неожиданный статус ответа");
        }
    }

    @Test
    @DisplayName("Добавление пользователя с null логином")
    void addNullLoginUser() {
        User user = User.builder()
                .email("kirill@kormilcev.ru")
                .login(null)
                .name("Кирилл")
                .birthday(LocalDate.of(1982,2,4))
                .build();
        HttpEntity<User> entity = new HttpEntity<>(user);
        try {
            ResponseEntity<User> response = restTemplate.postForEntity(url, entity, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Неожиданный статус ответа");
        }
    }

    @Test
    @DisplayName("Обновление пользователя с пустым логином")
    void updateBlankLoginUser() {
        userController.addData(user);
        User user1 = User.builder()
                .id(user.getId())
                .email("kirill@kormilcev.ru")
                .login("")
                .name("Кирилл")
                .birthday(LocalDate.of(1982,2,4))
                .build();
        HttpEntity<User> entity = new HttpEntity<>(user1);
        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Неожиданный статус ответа");
        }
    }

    @Test
    @DisplayName("Добавление пользователя с логином, содержащим пробелы")
    void addLoginWithSpaceUser() {
        User user = User.builder()
                .email("kirill@kormilcev.ru")
                .login("Это я")
                .name("Кирилл")
                .birthday(LocalDate.of(1982,2,4))
                .build();
        try {
            userController.addData(user);
        } catch (UserValidationException e) {
            assertEquals(UserValidationException.class, e.getClass(), "При добавлении пользователя" +
                    " не выброшено исключение.");
            assertEquals("В логине присутствуют пробелы.", e.getMessage(),
                    "Не верное сообщение в исключении");
        }
    }

    @Test
    @DisplayName("Добавление пользователя с пустым именем")
    void addEmptyNameUser() {
        User user = User.builder()
                .email("kirill@kormilcev.ru")
                .login("kirill")
                .name("")
                .birthday(LocalDate.of(1982,2,4))
                .build();
        try {
            userController.addData(user);
        } catch (UserValidationException e) {
            assertEquals(user.getLogin(), user.getName(), "При добавлении пользователя без имени не подставлен логин");
        }
    }

    @Test
    @DisplayName("Добавление пользователя с не корректной датой рождения")
    void addIncorrectBirthdayUser() {
        User user = User.builder()
                .email("kirill@kormilcev.ru")
                .login("kirill")
                .name("Кирилл")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        try {
            userController.addData(user);
        } catch (UserValidationException e) {
            assertEquals(UserValidationException.class, e.getClass(), "При добавлении пользователя" +
                    " не выброшено исключение.");
            assertEquals("Не корректная дата рождения.", e.getMessage(),
                    "Не верное сообщение в исключении");
        }
    }

    @Test
    @DisplayName("Обновление пользователя с почтой уже имеющейся в базе")
    void updateExistingEmailUser() {
        userController.addData(user);
        User user1 = User.builder()
                .id(user.getId())
                .email("kirill@yandex.ru")
                .login("kirill")
                .name("Кирилл")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        userController.addData(user1);
        User user2 = User.builder()
                .id(user.getId())
                .email("kirill@yandex.ru")
                .login("kirillka")
                .name("Кириллка")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        try {
            userController.updateData(user2);
        } catch (UserValidationException e) {
            assertEquals(UserValidationException.class, e.getClass(), "При добавлении пользователя" +
                    " не выброшено исключение.");
            assertEquals("Пользователь с такой почтой уже зарегистрирован в базе.", e.getMessage(),
                    "Не верное сообщение в исключении");
        }
    }
}