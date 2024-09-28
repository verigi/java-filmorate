package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectUserDetails;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
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
    public User deleteUser(Integer id) {
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
    public User getUser(Integer id) {
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

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        log.debug("User {} adding friend {}", userId, friendId);
        if (userId.equals(friendId)) throw new IncorrectUserDetails("Both ids are the same");
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        log.debug("User {} deleting friend {}", userId, friendId);
        if (userId.equals(friendId)) throw new IncorrectUserDetails("Both ids are the same");
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public Collection<User> getAllFriends(Integer id) {
        log.debug("Getting friend list of user {}", id);
        ArrayList<User> friendList = new ArrayList<>();
        User user = getUser(id);
        for (Integer friendId : user.getFriends()) {
            friendList.add(getUser(friendId));
        }
        return friendList;
    }

    @Override
    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        log.debug("Getting common friend list of users {} and {}", firstUserId, secondUserId);
        User user1 = getUser(firstUserId);
        User user2 = getUser(secondUserId);
        List<Integer> commonIds = user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .toList();

        List<User> commonFriends = new ArrayList<>();
        for (Integer friendId : commonIds) {
            commonFriends.add(getUser(friendId));
        }
        return commonFriends;
    }

    private Integer generateId() {
        return ++id;
    }
}
