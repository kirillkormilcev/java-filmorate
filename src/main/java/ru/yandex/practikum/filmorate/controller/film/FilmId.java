package ru.yandex.practikum.filmorate.controller.film;

class FilmId {

    private static int id = 1;

    private FilmId() {
    }

    public static int getId() {
        return id++;
    }
}
