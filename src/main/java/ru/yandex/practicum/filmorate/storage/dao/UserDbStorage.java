package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@Primary
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<User> userRowMapper = (ResultSet res, int rowNum) -> User.builder()
            .id(res.getInt("user_id"))
            .email(res.getString("email"))
            .login(res.getString("login"))
            .name(res.getString("name"))
            .birthday(res.getDate("birthday").toLocalDate())
            .build();

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);

        Integer newId = keyHolder.getKey().intValue();
        user.setId(newId);

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

        getUser(user.getId());
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User deleteUser(Integer id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        User user = getUser(id);

        jdbcTemplate.update(sql, id);
        return user;
    }

    @Override
    public User getUser(Integer id) {
        String sql = "SELECT u.user_id, u.name, u.email, u.login, u.birthday " +
                "FROM users AS u " +
                "WHERE u.user_id = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                User user = userRowMapper.mapRow(rs, rs.getRow());
                user.setFriends(extractFriends(id));
                return user;
            } else {
                log.warn("User id " + id + " was not found");
                throw new UserNotFoundException("User with ID " + id + " not found.");
            }
        });
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT u.user_id, u.name, u.email, u.login, u.birthday" +
                " FROM users AS u";
        List<User> users = jdbcTemplate.query(sql, userRowMapper);

        for (User user : users) {
            Set<Integer> friends = extractFriends(user.getId());
            user.setFriends(friends != null ? friends : new HashSet<>());
        }
        return users;
    }

    @Override
    public void deleteAllUsers() {
        String sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";

        getUser(userId);
        getUser(friendId);
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";

        getUser(userId);
        getUser(friendId);
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Collection<User> getAllFriends(Integer id) {
        String sql = "SELECT u.user_id, u.name, u.email, u.login, u.birthday " +
                "FROM users u " +
                "JOIN friends f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";

        getUser(id);
        List<User> friends = jdbcTemplate.query(sql, new Object[]{id}, userRowMapper);

        for (User friend : friends) {
            Set<Integer> friendFriends = extractFriends(friend.getId());
            friend.setFriends(friendFriends != null ? friendFriends : new HashSet<>());
        }

        return friends;
    }

    @Override
    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        String sql = "SELECT u.user_id, u.name, u.email, u.login, u.birthday " +
                "FROM users u " +
                "JOIN friends f1 ON u.user_id = f1.friend_id " +
                "JOIN friends f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        getUser(firstUserId);
        getUser(secondUserId);
        List<User> commonFriends = jdbcTemplate.query(sql, new Object[]{firstUserId, secondUserId}, userRowMapper);

        for (User commonFriend : commonFriends) {
            Set<Integer> friendFriends = extractFriends(commonFriend.getId());
            commonFriend.setFriends(friendFriends != null ? friendFriends : new HashSet<>());
        }

        return commonFriends;
    }

    private Set<Integer> extractFriends(Integer userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Integer> friendIds = jdbcTemplate.queryForList(sql, new Object[]{userId}, Integer.class);
        return new HashSet<>(friendIds);
    }
}
