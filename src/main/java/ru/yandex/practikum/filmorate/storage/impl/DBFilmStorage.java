package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.Genre;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        updateFilmLikesRating(film.getId());
        return film;
    }

    @Override
    public void addLikeUserToFilm(long filmId, long userId) { // добавить метод обновления лайков в таблице фильмов
        String sqlInsert = "insert into LIKES (FILM_ID, USER_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlInsert, filmId, userId);
        updateFilmLikesRating(filmId);
    }

    @Override
    public void removeLikeUserFromFilm(long filmId, long userId) { // добавить метод обновления лайков в таблице фильмов
        String sqlDelete = "delete from LIKES " +
                "where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlDelete, filmId, userId);
        updateFilmLikesRating(filmId);
    }

    @Override
    public List<Long> getAllFilmIds() {
        String sql = "select FILM_ID from FILMS";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlSelect = "select GENRE_ID, GENRE_NAME, DESCRIPTION from GENRES " +
                "where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlSelect, (rs, rowNum) -> makeGenre(rs), id);
    }

    @Override
    public List<MPA> getAllMPAs() {
        String sqlSelect = "select MPA_RATING_ID, RATING_NAME, DESCRIPTION from MPA_RATINGS";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeMPA(rs));
    }

    @Override
    public MPA getMPAById(int id) {
        String sqlSelect = "select MPA_RATING_ID, RATING_NAME, DESCRIPTION from MPA_RATINGS " +
                "where MPA_RATING_ID = ?";
        return jdbcTemplate.queryForObject(sqlSelect, (rs, rowNum) -> makeMPA(rs), id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlSelect = "select GENRE_ID, GENRE_NAME, DESCRIPTION from GENRES";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeGenre(rs));
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
                    .filmGenresId(getGenreIdsByFilmId(rs.getLong("FILM_ID")))
                    .MPARatingId(rs.getLong("MPA_RATING_ID"))
                    .build();
        } catch (SQLException | RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании фильма из строки БД.");
        }
    }

    /**
     * создать объект жанра из бд
     */
    private Genre makeGenre(ResultSet rs) {
        try {
            return Genre.builder()
                    .genreId(rs.getInt("GENRE_ID"))
                    .genreName(rs.getString("GENRE_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .build();
        } catch (SQLException | RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании жанра из строки БД.");
        }
    }

    /**
     * создать объект MPA рейтинга из бд
     */
    private MPA makeMPA(ResultSet rs) {
        try {
            return MPA.builder()
                    .MPAId(rs.getInt("MPA_RATING_ID"))
                    .MPAName(rs.getString("RATING_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .build();
        } catch (SQLException | RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании MPA рейтинга из строки БД.");
        }
    }

    /**
     * список id жанров фильма по его id
     */
    private List<Long> getGenreIdsByFilmId(long id) {
        String sqlSelect = "select GENRE_ID from FILM_GENRES " +
                "where FILM_ID = ?";
        return jdbcTemplate.queryForList(sqlSelect, Long.class, id);
    }

    /**
     * обновить количество лайков у фильма
     */
    private void updateFilmLikesRating (long id) {
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
    private long likesCountByFilmId (long id) {
        String sqlSelect = "select count(USER_ID) from LIKES " +
                "where FILM_ID = ?";
        return Objects.requireNonNullElse(jdbcTemplate.queryForObject(sqlSelect, long.class, id), 0L);
    }
}
