package ru.yandex.practikum.filmorate.storage.impl;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.IdGenerator;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.util.*;

@Component("InMemoryUserStorage")
@Getter
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new LinkedHashMap<>(); /* мапа пользователей */
    private final Map<Long, Set<User>> userFriendIds = new HashMap<>(); /* мапа множеств друзей пользователя */
    private final Map<Long, Set<Film>> likedFilmIds = new HashMap<>(); /* мапа понравившихся фильмов */
    private final IdGenerator idGenerator = new IdGenerator();

    @Override
    public List<User> getListOfUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(idGenerator.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        if (!userFriendIds.containsKey(userId)) { /* если множество друзей еще не создано */
            userFriendIds.put(userId, new HashSet<>()); /* то создаем */
        }
        userFriendIds.get(userId).add(users.get(friendId));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        userFriendIds.get(userId).remove(users.get(friendId));
    }

    @Override
    public User getUserById(long id) {
        return null;
    } /* заглушка реализации БД */

    @Override
    public Set<User> getUserFriendIds(long id) {
        return null;
    } /* заглушка реализации БД */

    @Override
    public List<Long> getAllUserIds() {
        return null;
    }/* заглушка реализации БД */
}
