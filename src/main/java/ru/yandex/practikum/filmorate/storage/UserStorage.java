package ru.yandex.practikum.filmorate.storage;

import lombok.Getter;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface UserStorage {
    Map<Long, User> users = new LinkedHashMap<>();
    IdGenerator idGenerator = new IdGenerator();

    /** список всех фильмов */
    List<User> getListOfUsers();

    /** добавить фильм */
    User addUser(User user);

    /** обновить фильм*/
    User updateUser(User user);

    /** геттер мапы пользователей*/
    Map<Long, User> getUsers();
}

