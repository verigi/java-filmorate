package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.utils.UserInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
public class InMemoryUserService implements UserInterface {
    private final HashMap<Integer, User> USER_STORAGE = new HashMap<>();
    private Integer id = 0;

    @Override
    public User addUser(User user) {
        log.debug("Adding user: " + user.getLogin());
        Integer userId = generateId();
        user.setId(userId);
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        USER_STORAGE.put(user.getId(), user);
        log.debug("User added: " + user.getLogin());
        return USER_STORAGE.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        log.debug("Updating user: " + user.getLogin());
        Optional<User> userOptional = USER_STORAGE.keySet().stream()
                .filter(x -> x == user.getId())
                .map(y -> USER_STORAGE.put(user.getId(), user))
                .findAny();
        if (userOptional.isPresent()) {
            log.debug("User`s " + user.getLogin() + " profile has been updated");
            return userOptional.get();
        } else {
            log.warn("User was not found");
            throw new UserNotFoundException("Incorrect user details");
        }
    }

    @Override
    public User deleteUserById(Integer id) {
        log.debug("Deleting user`s profile. Id: " + id);
        if (USER_STORAGE.containsKey(id)) {
            log.debug("User id " + id + " has been deleted");
            return USER_STORAGE.remove(id);
        } else {
            log.warn("User id " + id + " was not found");
            throw new UserNotFoundException("Incorrect id");
        }
    }

    @Override
    public User getUserById(Integer id) {
        log.debug("Getting user. Id: " + id);
        Optional<User> userOptional = USER_STORAGE.keySet().stream()
                .filter(x -> x == id)
                .map(y -> USER_STORAGE.get(id))
                .findAny();
        if (userOptional.isPresent()) {
            log.debug("User id " + id + " has been found");
            return userOptional.get();
        } else {
            log.warn("User id " + id + " was not found");
            throw new UserNotFoundException("Incorrect id");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        log.debug("Getting all user`s profiles");
        return new ArrayList<>(USER_STORAGE.values());
    }

    private Integer generateId() {
        return ++id;
    }
}
