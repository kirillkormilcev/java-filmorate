package ru.yandex.practikum.filmorate.controller.film;

/** генератор id для фильмов */
class FilmId {

    private static int id = 1;

    private FilmId() {
    }

    public static int getId() {
        return id++;
    }
}
