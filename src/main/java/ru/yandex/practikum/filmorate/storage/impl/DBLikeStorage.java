package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.storage.LikeStorage;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class DBLikeStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeUserToFilm(long filmId, long userId) {
        String sqlInsert = "insert into LIKES (FILM_ID, USER_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlInsert, filmId, userId);
        updateFilmLikesRating(filmId);
    }

    @Override
    public void removeLikeUserFromFilm(long filmId, long userId) {
        String sqlDelete = "delete from LIKES " +
                "where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlDelete, filmId, userId);
        updateFilmLikesRating(filmId);
    }

    @Override
    public void updateFilmLikesRating(long id) {
        String sqlMerge = "merge into FILMS (FILM_ID, LIKES_RATING)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlMerge,
                id,
                likesCountByFilmId(id)
        );
    }

    /**
     * количество лайков у фильма по его id
     */
    private long likesCountByFilmId(long id) {
        String sqlSelect = "select count(USER_ID) from LIKES " +
                "where FILM_ID = ?";
        return Objects.requireNonNullElse(jdbcTemplate.queryForObject(sqlSelect, long.class, id), 0L);
    }
}
