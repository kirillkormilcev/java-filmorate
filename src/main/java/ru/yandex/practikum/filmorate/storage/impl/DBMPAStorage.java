package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.model.film.MPA;
import ru.yandex.practikum.filmorate.storage.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DBMPAStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;

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
        } catch (SQLException | RuntimeException e) {
            throw new CustomSQLException("Ошибка при создании MPA рейтинга из строки БД." + "\n" +
                    Arrays.toString(e.getStackTrace()), e.getCause());
        }
    }
}
