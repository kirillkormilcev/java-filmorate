package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.exception.GenreValidationException;
import ru.yandex.practikum.filmorate.model.film.Genre;
import ru.yandex.practikum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DBGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        if (id <= 0) {
            throw new GenreValidationException("Значение индекса жанра передано меньше или равно 0.");
        }
        String sqlSelect = "select GENRE_ID, GENRE_NAME, DESCRIPTION from GENRES " +
                "where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlSelect, (rs, rowNum) -> makeGenre(rs), id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlSelect = "select GENRE_ID, GENRE_NAME, DESCRIPTION from GENRES";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeGenre(rs));
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
        } catch (SQLException | RuntimeException e) {
            throw new CustomSQLException("Ошибка при создании жанра из строки БД." + "\n" +
                    Arrays.toString(e.getStackTrace()), e.getCause());
        }
    }
}
