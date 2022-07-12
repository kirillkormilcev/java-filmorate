package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

public interface UserStorage {
    Map<Long, User> users = new LinkedHashMap<>();
    Map<Long, Set<User>> userFriendIdsMap = new HashMap<>();
    Map<Long, Set<Film>> likedFilmIdsMap = new HashMap<>();
    IdGenerator idGenerator = new IdGenerator();

    /** список всех фильмов */
    List<User> getListOfUsers();

    /** добавить фильм */
    User addUser(User user);

    /** обновить фильм*/
    User updateUser(User user);

    /** добавить друга */
    void addFriend (long userId, long friendId);

    /** удалить друга */
    void removeFriend (long userId, long friendId);

    /** добавить лайк */
    void addLikeFilmToUser(long userId, long filmId);

    /** удалить лайк */
    void removeLikeFilmFromUser(long userId, long filmId);

    /** геттер мапы пользователей*/
    Map<Long, User> getUserMap();

    /** геттер мапы друзей пользователя*/
    Map<Long, Set<User>> getUserFriendIdsMap();

    /** геттер мапы пролайканых фильмов пользователя*/
    Map<Long, Set<User>> getLikedFilmIdsMap();
}

