package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilm(@PathVariable("id") Integer id) {
        return service.deleteFilm(id);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Integer id) {
        return service.getFilm(id);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return service.getAllFilms();
    }

    @DeleteMapping
    public void deleteAllFilms() {
        service.deleteAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer id,
                        @PathVariable("userId") Integer userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer id,
                           @PathVariable("userId") Integer userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return service.getPopularFilms(count);
    }
}
