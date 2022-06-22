package ru.yandex.practikum.filmorate.controller.user;

class UserId {

    private static int id = 1;

    private UserId() {
    }

    public static int getId() {
        return id++;
    }
}
