package ru.yandex.practikum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

@Component
@Getter
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userMap = new LinkedHashMap<>(); /* мапа пользователей */
    private final Map<Long, Set<User>> userFriendIdsMap = new HashMap<>(); /* мапа множеств друзей пользователя */
    private final Map<Long, Set<Film>> likedFilmIdsMap = new HashMap<>(); /* мапа понравившихся фильмов */
    private final IdGenerator idGenerator = new IdGenerator();

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
        if (!userFriendIdsMap.containsKey(userId)) { /* если множество друзей еще не создано */
            userFriendIdsMap.put(userId, new HashSet<>()); /* то создаем */
        }
        userFriendIdsMap.get(userId).add(userMap.get(friendId));
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        userFriendIdsMap.get(userId).remove(userMap.get(friendId));
    }
}
