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
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.FilmStorage;
import ru.yandex.practikum.filmorate.storage.GenreStorage;
import ru.yandex.practikum.filmorate.storage.MPAStorage;

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
    private final GenreStorage genreStorage;
    private final MPAStorage mpaStorage;//todo заинжектил, не придумал как обойтись без методов getGenreById,
    // todo getGenreIdsByFilmId и getMPAById, односторонне вроде допустимо

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
            stmt.setString(2, film.getDescription());
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
            String sqlInsertGenres = "insert into FILM_GENRES (FILM_ID, GENRE_ID) " +
                    "values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertGenres,
                        film.getId(),
                        genre.getId()
                );
            }
        }
        /*if (film.getMPA() != null) {
            film.setMPA(getMPAById(film.getMPA().getId()));
        }*/
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
            sqlMerge = "insert into FILM_GENRES (FILM_ID, GENRE_ID) " +
                    "values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlMerge,
                        film.getId(),
                        genre.getId()
                );
            }
        }
        /*film.setMPA(getMPAById(film.getMPA().getId()));*/
        return film;
    }

    @Override
    public List<Long> getAllFilmIds() {
        String sql = "select FILM_ID from FILMS";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    /*@Override
    public List<MPA> getAllMPAs() {
        String sqlSelect = "select MPA_RATING_ID, RATING_NAME, DESCRIPTION from MPA_RATINGS";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeMPA(rs));
    }*/

    /*@Override
    public MPA getMPAById(int id) {
        String sqlSelect = "select MPA_RATING_ID, RATING_NAME, DESCRIPTION from MPA_RATINGS " +
                "where MPA_RATING_ID = ?";
        return jdbcTemplate.queryForObject(sqlSelect, (rs, rowNum) -> makeMPA(rs), id);
    }*/

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
                    .genres(new HashSet<>() {{
                        for (Integer genreId : genreStorage.getGenreIdsByFilmId(rs.getInt("FILM_ID"))) {
                            add(genreStorage.getGenreById(genreId));
                        }
                    }})
                    .MPA(mpaStorage.getMPAById(rs.getInt("MPA_RATING_ID")))
                    .build();
        } catch (RuntimeException e) {
            throw new CustomSQLException("Ошибка при создании фильма из строки БД." + "\n" +
                    Arrays.toString(e.getStackTrace()), e.getCause());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * создать объект MPA рейтинга из бд
     */
    /*private MPA makeMPA(ResultSet rs) {
        try {
            return MPA.builder()
                    .id(rs.getInt("MPA_RATING_ID"))
                    .name(rs.getString("RATING_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .build();
        } catch (SQLException | RuntimeException e) {
            throw new CustomSQLException("Ошибка при создании MPA рейтинга из строки БД." + "\n" +
                    Arrays.toString(e.getStackTrace()), e.getCause());
        }
    }*/

    /**
     * список id жанров фильма по его id
     */
    /*private List<Integer> getGenreIdsByFilmId(long id) {
        String sqlSelect = "select GENRE_ID from FILM_GENRES " +
                "where FILM_ID = ?";
        return jdbcTemplate.queryForList(sqlSelect, Integer.class, id);
    }*/
}
