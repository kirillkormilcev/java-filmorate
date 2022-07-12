package ru.yandex.practikum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practikum.filmorate.exception.UserValidationException;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public ResponseEntity<List<User>> getAllUsersFromStorage() {
        if (userStorage.getUserMap().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(userStorage.getListOfUsers(), HttpStatus.OK);
        }
    }

    public ResponseEntity<User> addUserToStorage (User user) {
        if (userValidation(user)) {
            userStorage.addUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); /* TODO вернуть разные коды в зависимости от ситуации*/
        }
    }

    public ResponseEntity<User> updateUserInStorage (User user) {
        if (userValidation(user)) {
            userStorage.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); /* TODO вернуть разные коды в зависимости от ситуации*/
        }
    }

    public ResponseEntity<HttpStatus> addFriendToUser (long userId, long friendId) {
        //TODO check, throw
        userStorage.addFriend(userId, friendId); /* добавить друга пользователю */
        userStorage.addFriend(friendId, userId); /* добавить пользователя другу */
        getUserById(userId).setFriendsCount(userStorage.getUserFriendIdsMap().get(userId).size());
        /* обновить количество друзей */
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> removeFriendFromUser (long userId, long friendId) {
        //TODO check, throw
        userStorage.removeFriend(userId, friendId); /* удалить друга у пользователя */
        userStorage.removeFriend(friendId, userId); /* удалить пользователя у друга */
        getUserById(userId).setFriendsCount(userStorage.getUserFriendIdsMap().get(userId).size());
        /* обновить количество друзей */
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Set<User>> getFriendsByUserId (long userId) {
        //TODO check, throw
        return new ResponseEntity<>(userStorage.getUserFriendIdsMap().get(userId), HttpStatus.OK);
    }

    public User getUserById (Long userId) {
        //TODO check, throw
        return userStorage.getUserMap().get(userId);
    }

    private boolean userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Попытка добавить или обновить пользователя с логином: '{}', содержащего пробелы.", user.getLogin());
            throw new UserValidationException("В логине присутствуют пробелы.");
        }
        if (user.getName().trim().isBlank()) {
            log.info("Пользователю с логином: '{}' назначено аналогичное имя.", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка добавить или обновить пользователя с логином: '{}' с некорректной датой рождения: '{}'.",
                    user.getLogin(), user.getBirthday());
            throw new UserValidationException("Не корректная дата рождения.");
        }
        for (User userAvailable : userStorage.getUserMap().values()) {
            if (user.getEmail().equals(userAvailable.getEmail())) {
                if (userAvailable.getId() == user.getId()) {
                    return true;
                } else {
                    log.warn("Попытка добавить или обновить пользователя с логином: '{}' с уже существующей в базе почтой: '{}'.",
                            user.getLogin(), user.getEmail());
                    throw new UserValidationException("Пользователь с такой почтой уже зарегистрирован в базе.");
                }
            }
        }
        return true;
    }

}
