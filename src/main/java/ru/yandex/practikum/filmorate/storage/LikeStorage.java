package ru.yandex.practikum.filmorate.storage;

public interface LikeStorage {

    /**
     * добавить лайк
     */
    void addLikeUserToFilm(long filmId, long userId);

    /**
     * удалить лайк
     */
    void removeLikeUserFromFilm(long filmId, long userId);

    /**
     * обновить количество лайков у фильма
     */
    void updateFilmLikesRating(long id);
}
