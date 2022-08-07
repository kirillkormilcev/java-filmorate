package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        String sqlSelect = "select FILM_ID, FILM_NAME, DESCRIPTION, DURATION, LIKES_RATING, " +
                "MPA_RATING_ID, RELEASE_DATE from FILMS";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(long id) {
        String sqlSelect = "select FILM_ID, FILM_NAME, DESCRIPTION, DURATION, LIKES_RATING, MPA_RATING_ID, " +
                "RELEASE_DATE from FILMS " +
                "where FILM_ID = ?";
        return jdbcTemplate.queryForObject(sqlSelect, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public Film addFilm(Film film) {
        String sqlInsert = "insert into FILMS (FILM_NAME, DESCRIPTION, DURATION, MPA_RATING_ID, RELEASE_DATE) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsert,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getMPARatingId(),
                Date.valueOf(film.getReleaseDate())
        );
        sqlInsert = "insert into FILM_GENRES (FILM_ID, GENRE_ID) " + // todo нарушается принцип транзакции, но не придумал
                "values (?, ?)";
        for (long genreId: film.getFilmGenresId()) {
            jdbcTemplate.update(sqlInsert,
                    film.getId(),
                    genreId
            );
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlMerge = "merge into FILMS (FILM_ID, FILM_NAME, DESCRIPTION, DURATION, MPA_RATING_ID, RELEASE_DATE) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlMerge,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getMPARatingId(),
                Date.valueOf(film.getReleaseDate())
        );
        String sqlDelete = "delete from FILM_GENRES " +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
        sqlMerge = "insert into FILM_GENRES (FILM_ID, GENRE_ID) " + // todo нарушается принцип транзакции, но не придумал
                "values (?, ?)";
        for (long genreId: film.getFilmGenresId()) {
            jdbcTemplate.update(sqlMerge,
                    film.getId(),
                    genreId
            );
        }
        return film;
    }

    @Override
    public void addLikeUserToFilm(long filmId, long userId) {

    }

    @Override
    public void removeLikeUserFromFilm(long filmId, long userId) {

    }

    @Override
    public List<Long> getAllFilmIds() {
        String sql = "select FILM_ID from FILMS";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public Map<Long, Film> getFilms() {
        return null;
    } // заглушка реализации в памяти

    @Override
    public Map<Long, Set<User>> getLikeIds() {
        return null;
    } // заглушка реализации в памяти

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
                    .filmGenresId(new ArrayList<>())
                    //todo взять из таблицы жанров фильмов, не забыть это значение там обновлять
                    .MPARatingId(rs.getLong("MPA_RATING_ID"))
                    //todo взять из таблицы фильмов, не забыть это значение там обновлять
                    .build();
        } catch (SQLException | RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании фильма из строки БД.");
        }
    }
}
