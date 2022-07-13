package ru.yandex.practikum.filmorate.storage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

@Component
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmMap = new LinkedHashMap<>(); /* мапа фильмов */
    Comparator<Film> comparatorSortByLikeCount = (o1, o2) -> { /* компаратор сортировки по количеству лайков */
        if (o2.getLikesCount() > o1.getLikesCount()) {
            return 1;
        } else if (o2.getLikesCount() < o1.getLikesCount()) {
            return -1;
        } else {
            return (int) (o2.getId() - o1.getId()); /* при равных лайках по id */
        }
    };
    private final SortedSet<Film> sortedByLikeCountFilmSet = new TreeSet<>(comparatorSortByLikeCount);
    /* множество фильмов, сортированных по количеству лайков */
    private final Map<Long, Set<User>> likeIdsMap = new HashMap<>(); /* мапа множеств лайкнувших пользователей */
    private final IdGenerator idGenerator = new IdGenerator();
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getListOfFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(idGenerator.getId());
        filmMap.put(film.getId(), film);
        if (film.getLikesCount() > 0) {
            sortedByLikeCountFilmSet.add(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmMap.put(film.getId(), film);
        if (film.getLikesCount() > 0) { /* TODO надо ли? может прилететь на обновление фильм с измененным количеством лайков */
            sortedByLikeCountFilmSet.add(film);
        }
        return film;
    }

    @Override
    public void addLikeUserToFilm(long filmId, long userId) {
        if (!likeIdsMap.containsKey(filmId)) { /* если множество лайкнувших пользователей еще не создано */
            likeIdsMap.put(filmId, new HashSet<>()); /* то создаем */
        }
        likeIdsMap.get(filmId).add(userStorage.getUserMap().get(userId));
    }

    @Override
    public void removeLikeUserFromFilm(long filmId, long userId) {
        likeIdsMap.get(filmId).remove(userStorage.getUserMap().get(userId));
    }

    @Override
    public void updateFilmInSortedByLikesSet(Film film) {
        if (film.getLikesCount() > 0) {
            sortedByLikeCountFilmSet.add(film);
        } else {
            sortedByLikeCountFilmSet.remove(film);
        }

    }
}
