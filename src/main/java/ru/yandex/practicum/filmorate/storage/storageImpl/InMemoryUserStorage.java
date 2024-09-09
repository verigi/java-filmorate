package ru.yandex.practicum.filmorate.storage.storageImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> storage = new HashMap<>();
    private Integer id = 0;

    @Override
    public User addUser(User user) {
        log.debug("Adding user: " + user.getLogin());
        Integer userId = generateId();
        user.setId(userId);
        storage.put(user.getId(), user);
        log.debug("User added: " + user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Updating user: " + user.getLogin());
        if (storage.containsKey(user.getId())) {
            storage.put(user.getId(), user);
            log.debug("User`s " + user.getLogin() + " profile has been updated");
            return user;
        } else {
            log.warn("User was not found");
            throw new UserNotFoundException("Incorrect user details");
        }
    }

    @Override
    public User deleteUserById(Integer id) {
        log.debug("Deleting user`s profile. Id: " + id);
        if (storage.containsKey(id)) {
            log.debug("User id " + id + " has been deleted");
            return storage.remove(id);
        } else {
            log.warn("User id " + id + " was not found");
            throw new UserNotFoundException("Incorrect id");
        }
    }

    @Override
    public User getUserById(Integer id) {
        log.debug("Getting user. Id: " + id);
        if (storage.containsKey(id)) {
            log.debug("User id " + id + " has been found");
            return storage.get(id);
        } else {
            log.warn("User id " + id + " was not found");
            throw new UserNotFoundException("Incorrect id");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        log.debug("Getting all user`s profiles");
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteAllUsers() {
        log.debug("Deleting all user`s profiles");
        storage.clear();
    }

    private Integer generateId() {
        return ++id;
    }
}
