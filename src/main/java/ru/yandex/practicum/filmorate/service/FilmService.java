package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.storage = filmStorage;
    }

    public Film addFilm(Film film) {
        log.debug("Adding film {} to database", film.getName());
        return storage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.debug("Updating film {} to database", film.getName());
        return storage.updateFilm(film);
    }

    public Film deleteFilm(Integer id) {
        log.debug("Deleting film {} from database", storage.getFilm(id).getName());
        return storage.deleteFilm(id);
    }

    public Film getFilm(Integer id) {
        log.debug("Getting film {} from database", storage.getFilm(id).getName());
        return storage.getFilm(id);
    }

    public Collection<Film> getAllFilms() {
        log.debug("Getting list of films");
        return storage.getAllFilms();
    }

    public void deleteAllFilms() {
        log.debug("Delete all films from database");
        storage.deleteAllFilms();
        log.debug("Delete successful");
    }

    // работа с лайками
    public void addLike(Integer filmId, Integer userId) {
        log.debug("User {} adding like to film {}", userId, filmId);
        storage.addLike(filmId, userId);
        log.debug("Like added successfully");
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.debug("User {} deleting like from film {}", userId, filmId);
        storage.deleteLike(filmId, userId);
        log.debug("Like deleted successfully");
    }

    public List<Film> getPopularFilms(Integer filmCount) {
        log.debug("Requesting the most popular film of {} count", filmCount);
        return storage.getPopularFilms(filmCount);
    }
}
