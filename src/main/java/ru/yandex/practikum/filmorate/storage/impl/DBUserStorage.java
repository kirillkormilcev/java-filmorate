package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.exception.NotFoundException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier
@RequiredArgsConstructor
public class DBUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<User> getListOfUsers() {
        String sql = "select USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY, FRIENDS_COUNT from USERS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUserById (long id) {
        checkUserId(id);
        String sql = "select USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY, FRIENDS_COUNT from USERS " +
                "where USER_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public User addUser(User user) {
        String sql = "insert into USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY, FRIENDS_COUNT)" +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getFriendsCount()
                );
        // TODO извлечь и присвоить id пользователю
        return user;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void addFriend(long userId, long friendId) {

    }

    @Override
    public void removeFriend(long userId, long friendId) {

    }

    @Override
    public Map<Long, User> getUsers() {
        return null;
    }

    @Override
    public Map<Long, Set<User>> getUserFriendIds() {
        return null;
    }

    @Override
    public Map<Long, Set<Film>> getLikedFilmIds() {
        return null;
    }

    private User makeUser (ResultSet rs){
        try {
            return User.builder()
                    .id(rs.getLong("USER_ID"))
                    .email(rs.getString("EMAIL"))
                    .login(rs.getString("LOGIN"))
                    .name(rs.getString("USER_NAME"))
                    .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                    .friendsCount(rs.getInt("FRIENDS_COUNT"))
                    .build();
        } catch (SQLException | RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании пользователя из строки БД.");
        }
    }

    private void checkUserId (long id) {
        String sql = "select USER_ID from USERS";
        List<Long> ids= jdbcTemplate.queryForList(sql, Long.class);
        if (!ids.contains(id)) {
            throw new NotFoundException("Пользователя с индексом: " + id + " нет в базе пользователей.");
        }
    }
}
