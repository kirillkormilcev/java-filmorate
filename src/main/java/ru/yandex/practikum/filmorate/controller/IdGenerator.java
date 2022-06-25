package ru.yandex.practikum.filmorate.controller;

import lombok.Setter;

@Setter
public class IdGenerator {

    private int id = 1;

    protected IdGenerator() {
    }

    public int getId() {
        return id++;
    }
}
