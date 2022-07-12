package ru.yandex.practikum.filmorate.storage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

@Component
@Getter
public class InMemoryUserStorage implements UserStorage{
    private final Map<Long, User> userMap = new LinkedHashMap<>();
    private final Map<Long, Set<User>> userFriendIdsMap = new HashMap<>();
    private final Map<Long, Set<Film>> likedFilmIdsMap = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();
    private final FilmStorage filmStorage;

    @Autowired
    public InMemoryUserStorage(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public List<User> getListOfUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(idGenerator.getId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        userFriendIdsMap.get(userId).add(userMap.get(friendId));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        userFriendIdsMap.get(userId).remove(userMap.get(friendId));
    }

    @Override
    public void addLikeFilmToUser(long userId, long filmId) {
        likedFilmIdsMap.get(userId).add(filmStorage.getFilmMap().get(filmId));
    }

    @Override
    public void removeLikeFilmFromUser(long userId, long filmId) {
        likedFilmIdsMap.get(userId).remove(filmStorage.getFilmMap().get(filmId));
    }
}
