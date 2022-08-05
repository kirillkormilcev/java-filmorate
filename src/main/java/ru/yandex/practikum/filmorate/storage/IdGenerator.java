package ru.yandex.practikum.filmorate.storage;

import lombok.Setter;

@Setter
public class IdGenerator {
    private long id = 1L;

    public IdGenerator() {
    }

    public long getId() {
        return id++;
    }
}
