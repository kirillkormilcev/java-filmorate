package ru.yandex.practikum.filmorate.controller.user;

/** генератор id для пользователей */
class UserId {

    private static int id = 1;

    private UserId() {
    }

    public static int getId() {
        return id++;
    }
}
