package ru.yandex.practikum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

@Component
@Slf4j
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmMap = new LinkedHashMap<>();
    Comparator<Film> comparatorSortByLikeCount = (o1, o2) -> {
        if (o2.getLikesCount() > o1.getLikesCount()) {
            return 1;
        } else if (o2.getLikesCount() < o1.getLikesCount()) {
            return -1;
        } else {
            return (int) (o2.getId() - o1.getId());
        }
    };
    private final SortedSet<Film> sortedByLikeCountFilmSet = new TreeSet<>(comparatorSortByLikeCount);
    private final Map<Long, Set<User>> likeIdsMap = new HashMap<>();
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
        log.info("Получен POST запрос к эндпоинту /{}s, успешно обработан.\n" +
                        "В базу добавлен фильм: '{}' с id: '{}'.", film.getDataType().toString().toLowerCase(Locale.ROOT),
                film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmMap.put(film.getId(), film);
        log.info("Получен PUT запрос к эндпоинту: /{}s, успешно обработан.\n" +
                        "В базе обновлен фильм: '{}' с id: '{}'.", film.getDataType().toString().toLowerCase(Locale.ROOT),
                film.getName(), film.getId());
        return film;
    }

    @Override
    public void addLikeUserToFilm(long filmId, long userId) {
        likeIdsMap.get(filmId).add(userStorage.getUserMap().get(userId));
    }

    @Override
    public void removeLikeUserFromFilm(long filmId, long userId) {
        likeIdsMap.get(filmId).remove(userStorage.getUserMap().get(userId));
    }
}
