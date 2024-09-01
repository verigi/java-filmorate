package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.utils.UserInterface;

import java.util.*;

/*
Привет, Ирек! Спасибо за комментарии и поправки!
Пара вопросов, если позволишь:
1. Я правильно понимаю, что повторное обращение за сущностью в хранилище нежелательно по причине потери
производительности?
2. Касаемо Optional и Stream - хотел "козырнуть", применить на практике) Тут такой вопрос к тебе, как к опытному
разработчику: насколько важно уметь решать задачи с Codewars и Leetcode? Дело в том, что простые задачи (kyu 8,7,6 -
если брать в пример Codewars) решаю достаточно легко/относительно легко, но вот более сложные даются прям со скрипом.
И вот, собственно, вопрос: что более практично - натаскивать себя, делать упор на задачи или разрабатывать дополнительно
сторонний пет-проект?
 */
@Slf4j
public class InMemoryUserService implements UserInterface {
    private final HashMap<Integer, User> service = new HashMap<>();
    private Integer id = 0;

    @Override
    public User addUser(User user) {
        log.debug("Adding user: " + user.getLogin());
        Integer userId = generateId();
        user.setId(userId);
        service.put(user.getId(), user);
        log.debug("User added: " + user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Updating user: " + user.getLogin());
        if (!service.get(user.getId()).equals(null)) {
            service.put(user.getId(), user);
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
        if (service.containsKey(id)) {
            log.debug("User id " + id + " has been deleted");
            return service.remove(id);
        } else {
            log.warn("User id " + id + " was not found");
            throw new UserNotFoundException("Incorrect id");
        }
    }

    @Override
    public User getUserById(Integer id) {
        log.debug("Getting user. Id: " + id);
        if (!service.get(id).equals(null)) {
            log.debug("User id " + id + " has been found");
            return service.get(id);
        } else {
            log.warn("User id " + id + " was not found");
            throw new FilmNotFoundException("Incorrect id");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        log.debug("Getting all user`s profiles");
        return new ArrayList<>(service.values());
    }

    @Override
    public void deleteAllUsers() {
        log.debug("Deleting all user`s profiles");
        service.clear();
    }

    private Integer generateId() {
        return ++id;
    }
}
