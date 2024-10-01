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

import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.*;

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

        user.setId(keyHolder.getKey().intValue());
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
    public User getUser(Integer id) {
        String sql = "SELECT u.user_id, u.name, u.email, u.login, u.birthday " +
                "FROM users AS u WHERE u.user_id = ?";

        User user = jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                return userRowMapper.mapRow(rs, rs.getRow());
            } else {
                log.warn("User id {} was not found", id);
                throw new UserNotFoundException("User with ID " + id + " not found.");
            }
        });
        user.setFriends(new LinkedHashSet<>(extractFriends().getOrDefault(id, new LinkedHashSet<>())));

        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT u.user_id, u.name, u.email, u.login, u.birthday FROM users AS u";

        List<User> users = jdbcTemplate.query(sql, userRowMapper);

        users.forEach(user ->
                user.setFriends(new LinkedHashSet<>(extractFriends()
                        .getOrDefault(user.getId(), new LinkedHashSet<>()))));
        return users;
    }

    @Override
    public User deleteUser(Integer id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        User user = getUser(id);
        jdbcTemplate.update(sql, id);
        return user;
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
    public Collection<User> getAllFriends(Integer userId) {
        String sql = "SELECT u.user_id, u.name, u.email, u.login, u.birthday " +
                "FROM users AS u JOIN friends AS f ON u.user_id = f.friend_id WHERE f.user_id = ?";

        getUser(userId);
        List<User> friends = jdbcTemplate.query(sql, new Object[]{userId}, userRowMapper);

        friends.forEach(friend ->
                friend.setFriends(new LinkedHashSet<>(extractFriends()
                        .getOrDefault(friend.getId(), new LinkedHashSet<>()))));

        return friends;
    }

    @Override
    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        String sql = "SELECT u.user_id, u.name, u.email, u.login, u.birthday " +
                "FROM users AS u JOIN friends AS f1 ON u.user_id = f1.friend_id " +
                "JOIN friends AS f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        getUser(firstUserId);
        getUser(secondUserId);
        List<User> commonFriends = jdbcTemplate.query(sql, new Object[]{firstUserId, secondUserId}, userRowMapper);

        Map<Integer, Set<Integer>> friendsMap = extractFriends();
        commonFriends.forEach(commonFriend ->
                commonFriend.setFriends(new LinkedHashSet<>(friendsMap.getOrDefault(commonFriend.getId(),
                        new LinkedHashSet<>()))));

        return commonFriends;
    }

    private Map<Integer, Set<Integer>> extractFriends() {
        String sql = "SELECT user_id, friend_id FROM friends";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        Map<Integer, Set<Integer>> friendsMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Integer userId = (Integer) row.get("user_id");
            Integer friendId = (Integer) row.get("friend_id");

            friendsMap.computeIfAbsent(userId, friend -> new LinkedHashSet<>()).add(friendId);
        }
        return friendsMap;
    }
}