package ru.yandex.practikum.filmorate.controller.film;

class FilmId {

    private static int id;

    private FilmId() {
        id = 1;
    }

    public static int getId() {
        return id++;
    }
}
