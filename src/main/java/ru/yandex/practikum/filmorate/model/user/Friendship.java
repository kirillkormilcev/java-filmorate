package ru.yandex.practikum.filmorate.model.user;

import lombok.Data;

/** класс дружбы, не использовал пока*/
@Data
public class Friendship {
    private final long id;
    private final long userId;
    private final long friendId;
    private boolean confirm;
}
