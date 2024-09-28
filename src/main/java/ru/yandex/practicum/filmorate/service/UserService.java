package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User deleteUserById(Integer id) {
        return userStorage.deleteUser(id);
    }

    public User getUserById(Integer id) {
        return userStorage.getUser(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getAllFriends(Integer id) {
        return userStorage.getAllFriends(id);
    }

    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        return userStorage.getCommonFriends(firstUserId, secondUserId);
    }

}
