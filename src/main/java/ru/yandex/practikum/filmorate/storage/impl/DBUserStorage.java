package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/** реализация хранилища в базе данных */
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
                user.getFriendsCount() // TODO вычислить из таблицы дружбы
                );
        // TODO извлечь и присвоить id пользователю бы
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "merge into USERS (USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY, FRIENDS_COUNT)" +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getFriendsCount() //TODO вычислить из таблицы дружбы
        );
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlInsert = "insert into FRIENDSHIPS (USER1_ID, FRIEND2_ID, CONFIRM) " +
                "values (?, ?, ?)";
        String[] checkFriendship = checkFriendship(userId, friendId);
        if (checkFriendship[0].equals("sameRequestFalse")) {
            throw new CustomSQLException("Запрос на дружбу уже отправлен!");
        } else if (checkFriendship[0].equals("sameRequestTrue")) {
            throw new CustomSQLException("Запрос на дружбу уже отправлен и подтвержден другой стороной!");
        } else if (checkFriendship[0].equals("notYetRequest") & checkFriendship[1].equals("firstRequest")) {
            jdbcTemplate.update(sqlInsert, userId, friendId, false); // добавляем этот запрос с флагом false
        } else if (checkFriendship[0].equals("notYetRequest") & checkFriendship[1].equals("oneSidedFriendship")) {
            jdbcTemplate.update(sqlInsert, userId, friendId, true); // добавляем его с флагом true
            String sqlDelete = "delete from FRIENDSHIPS " +
                    "where USER1_ID = ? and FRIEND2_ID = ?";
            jdbcTemplate.update(sqlDelete, friendId, userId); // удаляем старый противоположный запрос
            jdbcTemplate.update(sqlInsert, friendId, userId, true); // вместо него добавляем такой же с флагом true
        } else if (checkFriendship[0].equals("notYetRequest") & checkFriendship[1].equals("twoSidedFriendship")) {
            throw new CustomSQLException("Ошибка в БД: при первом запросе на дружбу, в базе уже есть ответный" +
                    " подтвержденный.");
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlDelete = "delete from FRIENDSHIPS " +
                "where USER1_ID = ? and FRIEND2_ID = ?";
        String[] checkFriendship = checkFriendship(userId, friendId);
        if (checkFriendship[0].equals("sameRequestFalse") & checkFriendship[1].equals("firstRequest")) {
            jdbcTemplate.update(sqlDelete, userId, friendId); // удаляем этот запрос
        } else if (checkFriendship[0].equals("sameRequestFalse") & checkFriendship[1].equals("oneSidedFriendship")) {
            throw new CustomSQLException("Ошибка БД: два противоположных не подтвержденных запроса.");
        } else if (checkFriendship[0].equals("sameRequestFalse") & checkFriendship[1].equals("twoSidedFriendship")) {
            throw new CustomSQLException("Ошибка БД: при удалении неподтвержденного запроса есть противоположный " +
                    "подтвержденный.");
        } else if (checkFriendship[0].equals("sameRequestTrue") & checkFriendship[1].equals("twoSidedFriendship")) {
            jdbcTemplate.update(sqlDelete, userId, friendId); // удаляем этот запрос
            jdbcTemplate.update(sqlDelete, friendId, userId); // противоположный тоже
            String sqlInsert = "insert into FRIENDSHIPS (USER1_ID, FRIEND2_ID, CONFIRM) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlInsert, friendId, userId, false); // заменяем true на false
        } else if (checkFriendship[0].equals("sameRequestTrue") & checkFriendship[1].equals("oneSidedFriendship")) {
            throw new CustomSQLException("Ошибка БД: при удалении подтвержденного запроса есть противоположный " +
                    "не подтвержденный.");
        } else if (checkFriendship[0].equals("sameRequestTrue") & checkFriendship[1].equals("firstRequest")) {
            throw new CustomSQLException("Ошибка БД: при удалении подтвержденного запроса в базе нет " +
                    "противоположного запроса.");
        } else if (checkFriendship[0].equals("notYetRequest")) {
            throw new CustomSQLException("Удаляемого запроса еще нет");
        }
    }

    /** проверка дружбы */
    private String[] checkFriendship(long userId, long friendId) {
        String sql = "select CONFIRM from FRIENDSHIPS " +
                "where USER1_ID = ? and FRIEND2_ID = ?";
        String direct = "";
        SqlRowSet userRowsNotRevert = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        if (!userRowsNotRevert.getBoolean("CONFIRM")) { // если такой запрос с флагом false уже отправлен
            direct = "sameRequestFalse";
        } else if (userRowsNotRevert.getBoolean("CONFIRM")) { // если такой запрос с флагом true уже отправлен
            direct = "sameRequestTrue";
        } else if (userRowsNotRevert.wasNull()) {
            direct = "notYetRequest";
        }
        if (direct.equals("")) {
            throw new CustomSQLException("Неожиданный результат при прямом запросе в проверке дружбы");
        }
        String revert = "";
        SqlRowSet userRowsRevert = jdbcTemplate.queryForRowSet(sql, friendId, userId);
        if (userRowsRevert.getBoolean("CONFIRM")) { // если есть противоположный запрос с флагом true
            revert = "twoSidedFriendship";
        } else if (!userRowsRevert.getBoolean("CONFIRM")) { // если есть противоположный запрос с флагом false
            revert = "oneSidedFriendship";
        } else if (userRowsRevert.wasNull()) { // если запроса еще нет
            revert = "firstRequest";
        }
        if (revert.equals("")) {
            throw new CustomSQLException("Неожиданный результат при обратном запросе в проверке дружбы");
        }
        return new String[] {direct, revert};
        //jdbcTemplate.queryForObject(sql, new Long[]{userId, friendId}, new int[]{1}, Long.class);
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

    @Override
    public List<Long> getAllUserIds() {
        String sql = "select USER_ID from USERS";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    /** создать объект пользователя из бд */
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
}
