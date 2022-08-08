package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.film.Genre;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier
@RequiredArgsConstructor
public class DBFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getListOfFilms() {
        String sqlSelect = "select FILM_ID, FILM_NAME, FILMS.DESCRIPTION, DURATION, LIKES_RATING, FILMS.MPA_RATING_ID, " +
                "RELEASE_DATE, MPA_RATINGS.MPA_RATING_ID, RATING_NAME from FILMS " +
                "left outer join MPA_RATINGS on FILMS.MPA_RATING_ID = MPA_RATINGS.MPA_RATING_ID";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(long id) {
        String sqlSelect = "select FILM_ID, FILM_NAME, FILMS.DESCRIPTION, DURATION, LIKES_RATING, FILMS.MPA_RATING_ID, " +
                "RELEASE_DATE, MPA_RATINGS.MPA_RATING_ID, RATING_NAME from FILMS " +
                "left outer  join MPA_RATINGS on FILMS.MPA_RATING_ID = MPA_RATINGS.MPA_RATING_ID " +
                "where FILM_ID = ?";
        return jdbcTemplate.queryForObject(sqlSelect, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public Film addFilm(Film film) {
        String sqlInsert = "insert into FILMS (FILM_NAME, DESCRIPTION, DURATION, MPA_RATING_ID, RELEASE_DATE) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlInsert, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2,  film.getDescription());
            stmt.setInt(3, film.getDuration());
            if (film.getMPA() == null) {
                throw new CustomSQLException("Поступил фильм с полем MPA = null.");
            }
            stmt.setInt(4, film.getMPA().getId());
            stmt.setDate(5, Date.valueOf(film.getReleaseDate()));
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.getGenres() != null) {
            String sqlInsertGenres = "insert into FILM_GENRES (FILM_ID, GENRE_ID) " + // todo нарушается принцип транзакции, но не придумал
                    "values (?, ?)";
            for (Genre genre: film.getGenres()) {
                jdbcTemplate.update(sqlInsertGenres,
                        film.getId(),
                        genre.getId()
                );
            }
        }
        if (film.getMPA() != null) {
            film.setMPA(getMPAById(film.getMPA().getId()));
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
                film.getMPA().getId(),
                Date.valueOf(film.getReleaseDate())
        );
        String sqlDelete = "delete from FILM_GENRES " +
                "where FILM_ID = ?";
        if (film.getGenres() != null) {
            jdbcTemplate.update(sqlDelete, film.getId());
            sqlMerge = "insert into FILM_GENRES (FILM_ID, GENRE_ID) " + // todo нарушается принцип транзакции, но не придумал
                    "values (?, ?)";
            for (Genre genre: film.getGenres()) {
                jdbcTemplate.update(sqlMerge,
                        film.getId(),
                        genre.getId()
                );
            }
        }
        updateFilmLikesRating(film.getId());
        film.setMPA(getMPAById(film.getMPA().getId()));
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
                    .likesRating(rs.getInt("LIKES_RATING"))
                    .genres(new HashSet<>(){{
                        for (Integer genreId: getGenreIdsByFilmId(rs.getInt("FILM_ID"))) {
                            add(getGenreById(genreId));
                        }
                    }})
                    .MPA(getMPAById(rs.getInt("MPA_RATING_ID")))
                    .build();
        } catch (RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании фильма из строки БД.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * создать объект жанра из бд
     */
    private Genre makeGenre(ResultSet rs) {
        try {
            return Genre.builder()
                    .id(rs.getInt("GENRE_ID"))
                    .name(rs.getString("GENRE_NAME"))
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
                    .id(rs.getInt("MPA_RATING_ID"))
                    .name(rs.getString("RATING_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .build();
        } catch (SQLException | RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании MPA рейтинга из строки БД.");
        }
    }

    /**
     * список id жанров фильма по его id
     */
    private List<Integer> getGenreIdsByFilmId(long id) {
        String sqlSelect = "select GENRE_ID from FILM_GENRES " +
                "where FILM_ID = ?";
        return jdbcTemplate.queryForList(sqlSelect, Integer.class, id);
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
