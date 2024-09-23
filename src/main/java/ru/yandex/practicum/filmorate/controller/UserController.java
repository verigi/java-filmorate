package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId,
                          @PathVariable("friendId") Integer friendId) {
        service.addFriend(userId, friendId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return service.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable("id") Integer id) {
        return service.deleteUserById(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer userId,
                             @PathVariable("friendId") Integer friendId) {
        service.deleteFriend(userId, friendId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriends(@PathVariable("id") Integer id) {
        return service.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") Integer firstId,
                                             @PathVariable("otherId") Integer secondId) {
        return service.getCommonFriends(firstId, secondId);
    }


    @DeleteMapping
    public void deleteAllUsers() {
        service.deleteAllUsers();
    }
}
