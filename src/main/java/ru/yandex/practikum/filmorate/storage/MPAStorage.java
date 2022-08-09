package ru.yandex.practikum.filmorate.storage;

import ru.yandex.practikum.filmorate.model.film.MPA;

import java.util.List;

public interface MPAStorage {

    /**
     * получить все MPA рейтинги
     */
    List<MPA> getAllMPAs();

    /**
     * получить MPA рейтинг по id
     */
    MPA getMPAById(int id);
}
