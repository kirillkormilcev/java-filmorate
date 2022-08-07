package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Qualifier
@RequiredArgsConstructor
public class DBFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getListOfFilms() {
        String sqlSelect = "select FILM_ID, FILM_NAME, DESCRIPTION, DURATION, LIKES_RATING, FILM_GENRES_ID, " +
                "MPA_RATING_ID, RELEASE_DATE from FILMS";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public void addLikeUserToFilm(long filmId, long userId) {

    }

    @Override
    public void removeLikeUserFromFilm(long filmId, long userId) {

    }

    @Override
    public Map<Long, Film> getFilms() {
        return null;
    }

    @Override
    public Map<Long, Set<User>> getLikeIds() {
        return null;
    }

    /**
     * создать объект фильма из бд
     */
    private Film makeFilm(ResultSet rs) {
        try {
            return Film.builder()
                    .id(rs.getLong("FILM_ID"))
                    .name(rs.getString("FILM_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration(rs.getInt("DURATION"))
                    .likesRating(rs.getLong("LIKES_RATING"))
                    //todo взять из таблицы фильмов, не забыть это значение там обновлять
                    .filmGenresId(rs.getLong("FILM_GENRES_ID"))
                    //todo взять из таблицы фильмов, не забыть это значение там обновлять
                    .MPARatingId(rs.getLong("MPA_RATING_ID"))
                    //todo взять из таблицы фильмов, не забыть это значение там обновлять
                    .build();
        } catch (SQLException | RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании фильма из строки БД.");
        }
    }
}
