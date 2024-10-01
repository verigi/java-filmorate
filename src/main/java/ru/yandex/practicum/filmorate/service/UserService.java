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
        log.debug("Add user {}", user.getName());
        return storage.addUser(user);
    }

    public User updateUser(User user) {
        log.debug("Updating user with id {}", user.getId());
        return storage.updateUser(user);
    }

    public User deleteUserById(Integer id) {
        log.debug("Delete user with id {}", id);
        return storage.deleteUser(id);
    }

    public User getUserById(Integer id) {
        log.debug("Getting user with id {} from database", id);
        return storage.getUser(id);
    }

    public Collection<User> getAllUsers() {
        log.debug("Getting list of users");
        return storage.getAllUsers();
    }

    public void deleteAllUsers() {
        log.debug("Delete all users");
        storage.deleteAllUsers();
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.debug("User with id {} add friend with id {}", userId, friendId);
        storage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        log.debug("User with id {} delete friend with id {}", userId, friendId);
        storage.deleteFriend(userId, friendId);
    }

    public Collection<User> getAllFriends(Integer id) {
        log.debug("Get friend-list of user with id {}", id);
        return storage.getAllFriends(id);
    }

    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        log.debug("Get common friend-list of user with id {} and user with id {}", firstUserId,secondUserId);
        return storage.getCommonFriends(firstUserId, secondUserId);
    }

}
