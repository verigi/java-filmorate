package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    User deleteUser(Integer id);

    User getUser(Integer id);

    Collection<User> getAllUsers();

    void deleteAllUsers();

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<User> getAllFriends(Integer id);

    Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId);
}
