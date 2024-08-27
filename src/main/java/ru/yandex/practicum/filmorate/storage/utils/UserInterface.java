package ru.yandex.practicum.filmorate.storage.utils;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserInterface {
    User addUser(User user);

    User updateUser(User user);

    User deleteUserById(Integer id);

    User getUserById(Integer id);

    Collection<User> getAllUsers();
}
