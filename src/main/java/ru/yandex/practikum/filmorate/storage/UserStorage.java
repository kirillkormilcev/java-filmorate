package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

public interface UserStorage {
    Map<Long, User> users = null;
    Map<Long, Set<User>> userFriendIdsMap = null;
    Map<Long, Set<Film>> likedFilmIdsMap = null;
    IdGenerator idGenerator = null;

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
    Map<Long, User> getUserMap();

    /**
     * геттер мапы друзей пользователя
     */
    Map<Long, Set<User>> getUserFriendIdsMap();

    /**
     * геттер мапы пролайканых фильмов пользователя
     */
    Map<Long, Set<Film>> getLikedFilmIdsMap();
}

