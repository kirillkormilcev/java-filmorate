package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

public interface UserStorage {

    /**
     * список всех фильмов
     */
    List<User> getListOfUsers();

    /**
     * добавить фильм
     */
    User addUser(User user);

    /**
     * обновить фильм
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
}

