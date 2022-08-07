package ru.yandex.practikum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practikum.filmorate.exception.CustomSQLException;
import ru.yandex.practikum.filmorate.model.film.Film;
import ru.yandex.practikum.filmorate.model.user.FriendshipCheck;
import ru.yandex.practikum.filmorate.model.user.User;
import ru.yandex.practikum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * реализация хранилища в базе данных
 */
@Repository
@Qualifier
@RequiredArgsConstructor
public class DBUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getListOfUsers() {
        String sqlSelect = "select USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY, FRIENDS_COUNT from USERS";
        return jdbcTemplate.query(sqlSelect, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUserById(long id) {
        String sqlSelect = "select USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY, FRIENDS_COUNT from USERS " +
                "where USER_ID = ?";
        return jdbcTemplate.queryForObject(sqlSelect, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public User addUser(User user) {
        String sqlInsert = "insert into USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY)" +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsert,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        // TODO извлечь и присвоить id пользователю бы, но надо ли
        // TODO keyHolder не понял почему-то не работал, значение null
        // TODO может из-за merge пользователей через data.sql, проверить
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlMerge = "merge into USERS (USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY, FRIENDS_COUNT)" +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlMerge,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                friendsCountByUserId(user.getId())
        );
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        FriendshipCheck[] checkFriendship = checkFriendship(userId, friendId);
        FriendshipCheck direct = checkFriendship[0];
        FriendshipCheck revert = checkFriendship[1];
        String sqlInsert = "insert into FRIENDSHIPS (USER1_ID, FRIEND2_ID, CONFIRM) " +
                "values (?, ?, ?)";
        if (direct == FriendshipCheck.DIRECT_NOT_YET & revert == FriendshipCheck.REVERT_FIRST) {
            jdbcTemplate.update(sqlInsert, userId, friendId, false); // добавляем этот запрос с флагом false
            updateUserFriendsCount(friendId); // обновляем количество друзей
        } else if (direct == FriendshipCheck.DIRECT_NOT_YET & revert == FriendshipCheck.REVERT_ONE_SIDED) {
            jdbcTemplate.update(sqlInsert, userId, friendId, true); // добавляем его с флагом true
            String sqlDelete = "delete from FRIENDSHIPS " +
                    "where USER1_ID = ? and FRIEND2_ID = ?";
            jdbcTemplate.update(sqlDelete, friendId, userId); // удаляем старый противоположный запрос
            jdbcTemplate.update(sqlInsert, friendId, userId, true); // вместо него добавляем такой же с флагом true
            updateUserFriendsCount(userId); // обновляем количество друзей
            updateUserFriendsCount(friendId);
        } else if (direct == FriendshipCheck.DIRECT_SAME_FALSE) {
            throw new CustomSQLException("Запрос на дружбу уже отправлен!");
        } else if (direct == FriendshipCheck.DIRECT_SAME_TRUE) {
            throw new CustomSQLException("Запрос на дружбу уже отправлен и подтвержден другой стороной!");
        } else if (direct == FriendshipCheck.DIRECT_NOT_YET & revert == FriendshipCheck.REVERT_TWO_SIDED) {
            throw new CustomSQLException("Ошибка в БД: при первом запросе на дружбу, в базе уже есть ответный" +
                    " подтвержденный.");
        }
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        FriendshipCheck[] checkFriendship = checkFriendship(userId, friendId);
        FriendshipCheck direct = checkFriendship[0];
        FriendshipCheck revert = checkFriendship[1];
        String sqlDelete = "delete from FRIENDSHIPS " +
                "where USER1_ID = ? and FRIEND2_ID = ?";
        if (direct == FriendshipCheck.DIRECT_SAME_FALSE & revert == FriendshipCheck.REVERT_FIRST) {
            jdbcTemplate.update(sqlDelete, userId, friendId); // удаляем этот запрос
            updateUserFriendsCount(friendId); // обновляем количество друзей
        } else if (direct == FriendshipCheck.DIRECT_SAME_TRUE & revert == FriendshipCheck.REVERT_TWO_SIDED) {
            jdbcTemplate.update(sqlDelete, userId, friendId); // удаляем этот запрос
            jdbcTemplate.update(sqlDelete, friendId, userId); // противоположный тоже
            String sqlInsert = "insert into FRIENDSHIPS (USER1_ID, FRIEND2_ID, CONFIRM) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlInsert, friendId, userId, false); // заменяем true на false
            updateUserFriendsCount(userId); // обновляем количество друзей
            updateUserFriendsCount(friendId);
        } else if (direct == FriendshipCheck.DIRECT_SAME_FALSE & revert == FriendshipCheck.REVERT_ONE_SIDED) {
            throw new CustomSQLException("Ошибка БД: два противоположных не подтвержденных запроса.");
        } else if (direct == FriendshipCheck.DIRECT_SAME_FALSE & revert == FriendshipCheck.REVERT_TWO_SIDED) {
            throw new CustomSQLException("Ошибка БД: при удалении неподтвержденного запроса есть противоположный " +
                    "подтвержденный.");
        } else if (direct == FriendshipCheck.DIRECT_SAME_TRUE & revert == FriendshipCheck.REVERT_ONE_SIDED) {
            throw new CustomSQLException("Ошибка БД: при удалении подтвержденного запроса есть противоположный " +
                    "не подтвержденный.");
        } else if (direct == FriendshipCheck.DIRECT_SAME_TRUE & revert == FriendshipCheck.REVERT_FIRST) {
            throw new CustomSQLException("Ошибка БД: при удалении подтвержденного запроса в базе нет " +
                    "противоположного запроса.");
        } else if (direct == FriendshipCheck.DIRECT_NOT_YET) {
            throw new CustomSQLException("Удаляемого запроса еще нет");
        }
    }

    @Override
    public Set<User> getUserFriendIds(long id) {
        String sqlSelect = "select USER1_ID, FRIEND2_ID from FRIENDSHIPS " +
                "where FRIEND2_ID = ?";
        return jdbcTemplate.queryForStream(sqlSelect, (rs, rowNum) -> getUserById(rs.getLong("USER1_ID")), id)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Long> getAllUserIds() {
        String sql = "select USER_ID from USERS";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public Map<Long, User> getUsers() {
        return null;
    } /* заглушка реализации в памяти */

    @Override
    public Map<Long, Set<Film>> getLikedFilmIds() {
        return null;
    } /* заглушка реализации в памяти */

    /**
     * создать объект пользователя из бд
     */
    private User makeUser(ResultSet rs) {
        try {
            return User.builder()
                    .id(rs.getLong("USER_ID"))
                    .email(rs.getString("EMAIL"))
                    .login(rs.getString("LOGIN"))
                    .name(rs.getString("USER_NAME"))
                    .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                    .friendsCount(friendsCountByUserId(rs.getLong("USER_ID")))
                    /* TODO здесь вычисляю, но можно и из таблицы пользователей взять, там тоже вычисляется
                    TODO оставил пока оба способа */
                    .build();
        } catch (SQLException | RuntimeException e) { // TODO правильный ли отлов ошибок
            throw new CustomSQLException("Ошибка при создании пользователя из строки БД.");
        }
    }

    /**
     * проверка дружбы
     */
    private FriendshipCheck[] checkFriendship(long userId, long friendId) {
        FriendshipCheck direct = null;
        String sql = "select CONFIRM from FRIENDSHIPS " +
                "where USER1_ID = ? and FRIEND2_ID = ?";
        SqlRowSet userRowsDirect = jdbcTemplate.queryForRowSet(sql, userId, friendId); // прямой запрос
        if (userRowsDirect.next()) {
            if (!userRowsDirect.getBoolean("CONFIRM")) {
                // если такой запрос с флагом false уже отправлен
                direct = FriendshipCheck.DIRECT_SAME_FALSE;
            } else if (userRowsDirect.getBoolean("CONFIRM")) {
                // если такой запрос с флагом true уже отправлен
                direct = FriendshipCheck.DIRECT_SAME_TRUE;
            }
        } else {
            direct = FriendshipCheck.DIRECT_NOT_YET;
        }
        if (direct == null) {
            throw new CustomSQLException("Неожиданный результат при прямом запросе в проверке дружбы");
        }
        FriendshipCheck revert = null;
        SqlRowSet userRowsRevert = jdbcTemplate.queryForRowSet(sql, friendId, userId); // обратный запрос
        if (userRowsRevert.next()) {
            if (userRowsRevert.getBoolean("CONFIRM")) {
                // если есть противоположный запрос с флагом true
                revert = FriendshipCheck.REVERT_TWO_SIDED;
            } else if (!userRowsRevert.getBoolean("CONFIRM")) {
                // если есть противоположный запрос с флагом false
                revert = FriendshipCheck.REVERT_ONE_SIDED;
            }
        } else {
            revert = FriendshipCheck.REVERT_FIRST;
        }
        if (revert == null) {
            throw new CustomSQLException("Неожиданный результат при обратном запросе в проверке дружбы");
        }
        return new FriendshipCheck[]{direct, revert};
    }

    /**
     * количество друзей пользователя
     */
    private long friendsCountByUserId(long id) {
        String sqlSelect = "select count(FRIEND2_ID) from FRIENDSHIPS " +
                "where FRIEND2_ID = ?";
        return Objects.requireNonNullElse(jdbcTemplate.queryForObject(sqlSelect, long.class, id), 0L);
    }

    /**
     * редактировать количество друзей в таблице пользователей
     */
    private void updateUserFriendsCount(long id) {
        String sqlMerge = "merge into USERS (USER_ID, FRIENDS_COUNT)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlMerge,
                id,
                friendsCountByUserId(id)
        );

    }
}
