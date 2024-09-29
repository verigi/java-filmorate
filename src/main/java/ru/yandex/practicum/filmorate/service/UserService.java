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
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User addUser(User user) {
        return storage.addUser(user);
    }

    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    public User deleteUserById(Integer id) {
        return storage.deleteUser(id);
    }

    public User getUserById(Integer id) {
        return storage.getUser(id);
    }

    public Collection<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public void deleteAllUsers() {
        storage.deleteAllUsers();
    }

    public void addFriend(Integer userId, Integer friendId) {
        storage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        storage.deleteFriend(userId, friendId);
    }

    public Collection<User> getAllFriends(Integer id) {
        return storage.getAllFriends(id);
    }

    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        return storage.getCommonFriends(firstUserId, secondUserId);
    }

}
