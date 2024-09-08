package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectUserDetails;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserService userStorage;

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User deleteUserById(Integer id) {
        return userStorage.deleteUserById(id);
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) throw new IncorrectUserDetails("Both ids are the same");
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) throw new IncorrectUserDetails("Both ids are the same");
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Collection<User> getAllFriends(Integer id) {
        ArrayList<User> friendList = new ArrayList<>();
        User user = userStorage.getUserById(id);
        for (Integer friendId : user.getFriends()) {
            friendList.add(userStorage.getUserById(friendId));
        }
        return friendList;
    }

    public Collection<User> getCommonFriends(Integer firstUserId, Integer secondUserId) {
        User user1 = userStorage.getUserById(firstUserId);
        User user2 = userStorage.getUserById(secondUserId);
        List<Integer> commonIds = user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .toList();

        List<User> commonFriends = new ArrayList<>();
        for (Integer friendId : commonIds) {
            commonFriends.add(userStorage.getUserById(friendId));
        }
        return commonFriends;
    }
}
