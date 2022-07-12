package ru.yandex.practikum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.film.Film;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public interface FilmStorage {
    Map<Integer, Film> films = new LinkedHashMap<>();
    IdGenerator idGenerator = new IdGenerator();

    /** список всех фильмов */
    List<Film> getListOfFilms();

    /** добавить фильм */
    Film addFilm(Film film);

    /** обновить фильм*/
    Film updateFilm(Film film);

    /** геттер мапы фильмов*/
    Map<Integer, Film> getFilms();
}

