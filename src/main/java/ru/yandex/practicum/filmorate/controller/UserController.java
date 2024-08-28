package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserService service = new InMemoryUserService();

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return service.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable("id") Integer id) {
        return service.deleteUserById(id);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return service.getAllUsers();
    }

    @DeleteMapping
    public void deleteAllUsers() {
        service.deleteAllUsers();
    }
}
