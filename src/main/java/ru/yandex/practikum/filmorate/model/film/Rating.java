package ru.yandex.practikum.filmorate.model.film;

public enum Rating {
    G ("Без ограничений"),
    PG ("Рекомендуется детям с родителями"),
    PG13 ("Детям до 13 лет не желательно"),
    R ("Лицам до 17 лет только в присутствии взрослого"),
    NC17 ("Лицам до 18 лет запрещено");

    private final String title;

    Rating(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
