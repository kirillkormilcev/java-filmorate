package ru.yandex.practikum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practikum.filmorate.exception.UserValidationException;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService userService = new UserService(new InMemoryUserStorage());
    User user1 = User.builder()
            .email("kirill@kormilcev.ru")
            .login("kirill")
            .name("Кирилл")
            .birthday(LocalDate.of(1982,2,4))
            .build();
    User user2 = User.builder()
            .email("jkgjg@ssh.ru")
            .login("hkjhkjh")
            .name("Пплополо")
            .birthday(LocalDate.of(1981,2,4))
            .build();
    User userWithSpacesInLogin = User.builder()
            .email("jkgjg@ssfdgsh.ru")
            .login("hkjh kjh")
            .name("Пплопарроло")
            .birthday(LocalDate.of(1980,2,4))
            .build();
    User userEmptyName = User.builder()
            .email("jkdfgjg@ssfdgsh.ru")
            .login("jhfkjh")
            .name(" ")
            .birthday(LocalDate.of(1979,2,4))
            .build();

    User userFutureBirthdate = User.builder()
            .email("jkdfjhgjg@ssfdgsh.ru")
            .login("jhfkbmnjh")
            .name("ырыпароапыр")
            .birthday(LocalDate.of(1079,2,4))
            .build();

    User userWithExistEmail1 = User.builder()
            .email("hggjgkjmn@ioyure.ru")
            .login("glkglkg")
            .name("лоплпп")
            .birthday(LocalDate.of(1962,2,4))
            .build();

    User userWithExistEmail2 = User.builder()
            .email("hgasdfgdfgjgkjmn@ioyure.ru")
            .login("glsddkdfgglkg")
            .name("лопвываалпп")
            .birthday(LocalDate.of(1942,2,4))
            .build();

    User userWithExistEmail3 = User.builder()
            .email("hggjgkjmn@ioyure.ru")
            .login("glsddkglkg")
            .name("лопвыалпп")
            .birthday(LocalDate.of(1952,2,4))
            .build();

    @Test
    void addNormalUser() {
        int countBefore = userService.getAllUsersFromStorage().size();
        userService.addUserToStorage(user1);
        assertEquals(countBefore, userService.getAllUsersFromStorage().size() - 1);
    }

    @Test
    void addUserWithSpacesInLogin() {
        try {
            userService.addUserToStorage(userWithSpacesInLogin);
        } catch (UserValidationException e) {
            assertEquals("В логине hkjh kjh присутствуют пробелы.", e.getMessage());
        }
    }

    @Test
    void addEmptyLoginUser() {
        userService.addUserToStorage(userEmptyName);
        assertEquals(userEmptyName.getLogin(), userEmptyName.getName());
    }

    @Test
    void getAllUsersFromStorage() {
        assertEquals(new ArrayList<>(userService.getUserStorage().getUsers().values()),
                userService.getAllUsersFromStorage());
    }

    @Test
    void getUserById() {
        userService.addUserToStorage(user2);
        assertEquals(userService.getUserStorage().getUsers().get(user2.getId()), user2);
    }

    @Test
    void updateFutureBirthdateUser() {
        userService.addUserToStorage(userFutureBirthdate);
        userFutureBirthdate.setBirthday(LocalDate.now().plusDays(2));
        try {
            userService.updateUserInStorage(userFutureBirthdate);
        } catch (UserValidationException e) {
            assertEquals("Не корректная дата рождения: 2022-07-16 у пользователя: jhfkbmnjh.", e.getMessage());
        }
    }

    @Test
    void updateUserWithExistEmail() {
        userService.addUserToStorage(userWithExistEmail1);
        userService.addUserToStorage(userWithExistEmail2);
        userWithExistEmail3.setId(userWithExistEmail2.getId());
        try {
            userService.updateUserInStorage(userWithExistEmail3);
        } catch (UserValidationException e) {
            assertEquals("Пользователь с почтой hggjgkjmn@ioyure.ru уже зарегистрирован в базе.", e.getMessage());
        }
    }

    @Test
    void addAndRemoveFriendToUserNormal() {
        userService.getUserStorage().getUsers().clear();
        user1.setFriendsCount(0);
        user2.setFriendsCount(0);
        userService.addUserToStorage(user1); //1
        userService.addUserToStorage(user2); //2
        userService.addFriendToUser(1, 2);
        assertEquals(new ArrayList<>(userService.getFriendsByUserId(1)).get(0), user2);
        assertEquals(new ArrayList<>(userService.getFriendsByUserId(2)).get(0), user1);
        assertEquals(1, user1.getFriendsCount());
        assertEquals(1, user2.getFriendsCount());
        userService.removeFriendFromUser(1, 2);
        assertEquals(0, userService.getFriendsByUserId(1).size());
        assertEquals(0, userService.getFriendsByUserId(2).size());
        assertEquals(0, user1.getFriendsCount());
        assertEquals(0, user2.getFriendsCount());
    }

    @Test
    void getFriendsByUserId() {
        userService.getUserStorage().getUsers().clear();
        userService.addUserToStorage(user1); //1
        userService.addUserToStorage(user2); //2
        userService.addUserToStorage(userWithExistEmail1); //3
        userService.addFriendToUser(1, 2);
        userService.addFriendToUser(1, 3);
        assertEquals(new ArrayList<>(userService.getFriendsByUserId(1)), new ArrayList<>(List.of(user2, userWithExistEmail1)));
        userService.removeFriendFromUser(1, 2);
        userService.removeFriendFromUser(1, 3);
    }

    @Test
    void getCommonFriends() {
        userService.getUserStorage().getUsers().clear();
        userService.addUserToStorage(user1); //1
        userService.addUserToStorage(user2); //2
        userService.addUserToStorage(userWithExistEmail1); //3
        userService.addFriendToUser(1, 2);
        userService.addFriendToUser(1, 3);
        assertEquals(new ArrayList<>(userService.getCommonFriends(2, 3)), new ArrayList<>(List.of(user1)));
        userService.removeFriendFromUser(1, 2);
        userService.removeFriendFromUser(1, 3);
    }
}