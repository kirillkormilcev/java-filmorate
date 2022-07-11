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
    private final Map<Long, Film> films = new LinkedHashMap<>(); /* мапа фильмов */
    private final Map<Long, Set<User>> likeIds = new HashMap<>(); /* мапа множеств лайкнувших пользователей */
    private final IdGenerator idGenerator = new IdGenerator();
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getListOfFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(idGenerator.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLikeUserToFilm(long filmId, long userId) {
        if (!likeIds.containsKey(filmId)) { /* если множество лайкнувших пользователей еще не создано */
            likeIds.put(filmId, new HashSet<>()); /* то создаем */
        }
        likeIds.get(filmId).add(userStorage.getUsers().get(userId));
    }

    @Override
    public void removeLikeUserFromFilm(long filmId, long userId) {
        likeIds.get(filmId).remove(userStorage.getUsers().get(userId));
    }
}
