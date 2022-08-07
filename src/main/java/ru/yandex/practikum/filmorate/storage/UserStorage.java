package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

public interface UserStorage {

    /**
     * список всех пользователей
     */
    List<User> getListOfUsers();

    /**
     * найти пользователя по id
     */
    User getUserById(long id);


    /**
     * добавить пользователя
     */
    User addUser(User user);

    /**
     * обновить пользователя
     */
    User updateUser(User user);

    /**
     * добавить друга
     */
    void addFriend(long userId, long friendId);

    /**
     * удалить друга
     */
    void removeFriend(long userId, long friendId);

    /**
     * геттер мапы пользователей
     */
    Map<Long, User> getUsers();

    /**
     * геттер мапы друзей пользователя
     */
    Map<Long, Set<User>> getUserFriendIds();

    /**
     * геттер мапы пролайканых фильмов пользователя
     */
    Map<Long, Set<Film>> getLikedFilmIds();

    /** получить id всех пользователей*/
    List<Long> getAllUserIds();
}

