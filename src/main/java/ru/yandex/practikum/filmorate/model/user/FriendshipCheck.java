package ru.yandex.practikum.filmorate.model.user;

/**
 * статусы при проверке дружбы
 */
public enum FriendshipCheck {
    DIRECT_SAME_FALSE,
    DIRECT_SAME_TRUE,
    DIRECT_NOT_YET,
    REVERT_TWO_SIDED,
    REVERT_ONE_SIDED,
    REVERT_FIRST
}
