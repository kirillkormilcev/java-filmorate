package ru.yandex.practikum.filmorate.model.user;

import lombok.Data;

@Data
public class Friendship {
    private final long id;
    private final long userId;
    private final long friendId;
    private boolean confirm;
}
