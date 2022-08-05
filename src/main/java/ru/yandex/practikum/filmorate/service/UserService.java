package ru.yandex.practikum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practikum.filmorate.exception.NotFoundException;
import ru.yandex.practikum.filmorate.exception.UserValidationException;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
public class UserService {
    @Getter //для Junit тестов
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * список всех пользователей в хранилище
     */
    public List<User> getAllUsersFromStorage() {
        return userStorage.getListOfUsers();
    }

    /**
     * пользователь по Id
     */
    public User getUserById(Long userId) {
        checkUserId(userId);
        return userStorage.getUsers().get(userId);
    }

    /**
     * добавить пользователя в хранилище
     */
    public User addUserToStorage(User user) {
        userValidation(user);
        userStorage.addUser(user);
        return user;

    }

    /**
     * обновить пользователя в хранилище
     */
    public User updateUserInStorage(User user) {
        checkUserId(user.getId());
        userValidation(user);
        userStorage.updateUser(user);
        return user;
    }

    /**
     * добавить друга пользователю
     */
    public User addFriendToUser(long userId, long friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        userStorage.addFriend(userId, friendId); /* добавить друга пользователю */
        userStorage.addFriend(friendId, userId); /* добавить пользователя другу */
        getUserById(userId).setFriendsCount(userStorage.getUserFriendIds().get(userId).size());
        /* обновить количество друзей пользователя*/
        getUserById(friendId).setFriendsCount(userStorage.getUserFriendIds().get(friendId).size());
        /* обновить количество друзей у друга*/
        return getUserById(userId);
    }

    /**
     * удалить друга у пользователя
     */
    public User removeFriendFromUser(long userId, long friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        userStorage.removeFriend(userId, friendId); /* удалить друга у пользователя */
        userStorage.removeFriend(friendId, userId); /* удалить пользователя у друга */
        getUserById(userId).setFriendsCount(userStorage.getUserFriendIds().get(userId).size());
        /* обновить количество друзей */
        getUserById(friendId).setFriendsCount(userStorage.getUserFriendIds().get(friendId).size());
        /* обновить количество друзей у друга*/
        return getUserById(userId);
    }

    /**
     * множество друзей пользователя
     */
    public Set<User> getFriendsByUserId(long userId) {
        checkUserId(userId);
        if (!userStorage.getUserFriendIds().containsKey(userId)) { /* если множество друзей еще не создано в мапе*/
            return new HashSet<>(); /* то вернуть пустое множество */
        } else {
            return userStorage.getUserFriendIds().get(userId);
        }
    }

    /**
     * общие друзья двух пользователей
     */
    public List<User> getCommonFriends(long userId, long otherId) {
        checkUserId(userId);
        checkUserId(otherId);
        Set<User> commonUserFriends = new HashSet<>(getFriendsByUserId(userId)); /* множество друзей пользователя */
        Set<User> otherFriends = new HashSet<>(getFriendsByUserId(otherId)); /* множество друзей другого пользователя */
        commonUserFriends.retainAll(otherFriends); /* пересечение этих множеств (общие друзья) */
        if (commonUserFriends.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(commonUserFriends);
        }
    }

    /**
     * проверка пользователя
     */
    private void userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new UserValidationException("В логине " + user.getLogin() + " присутствуют пробелы.");
        }
        if (user.getName().trim().isBlank()) {
            log.info("Пользователю с логином: '{}' назначено аналогичное имя.", user.getLogin()); //TODO надо ли?
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new UserValidationException("Не корректная дата рождения: " + user.getBirthday() + " у пользователя: "
                    + user.getLogin() + ".");
        }
        for (User userAvailable : userStorage.getUsers().values()) {
            if (user.getEmail().equals(userAvailable.getEmail())) {
                if (userAvailable.getId() == user.getId()) {
                    return;
                } else {
                    throw new UserValidationException("Пользователь с почтой " + user.getEmail()
                            + " уже зарегистрирован в базе.");
                }
            }
        }
    }

    /**
     * проверка наличия id пользователя в базе
     */
    private void checkUserId(long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователя с индексом: " + userId + " нет в базе пользователей.");
        }
    }
}
