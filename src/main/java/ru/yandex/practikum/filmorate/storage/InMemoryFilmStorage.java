package ru.yandex.practikum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.film.Film;

import java.util.*;

@Component
@Slf4j
@Getter
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    @Override
    public List<Film> getListOfFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(idGenerator.getId());
        films.put(film.getId(), film);
        log.info("Получен POST запрос к эндпоинту /{}s, успешно обработан.\n" +
                        "В базу добавлен фильм: '{}' с id: '{}'.", film.getDataType().toString().toLowerCase(Locale.ROOT),
                film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Получен PUT запрос к эндпоинту: /{}s, успешно обработан.\n" +
                        "В базе обновлен фильм: '{}' с id: '{}'.", film.getDataType().toString().toLowerCase(Locale.ROOT),
                film.getName(), film.getId());
        return film;
    }
}
