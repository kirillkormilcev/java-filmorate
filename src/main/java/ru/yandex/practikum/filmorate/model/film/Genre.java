package ru.yandex.practikum.filmorate.model.film;

public enum Genre {
    COMEDY ("Комедия"),
    DRAMA ("Драма"),
    CARTOON ("Мультфильм"),
    THRILLER ("Триллер"),
    DOCUMENTARY ("Документальный"),
    ACTION ("Боевик");

    private final String title;

    Genre(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
