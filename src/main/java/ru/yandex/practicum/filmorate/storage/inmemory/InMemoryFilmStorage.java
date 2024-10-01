package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> storage = new HashMap<>();
    private final InMemoryUserStorage uStorage = new InMemoryUserStorage();
    private Integer id = 0;

    @Override
    public Film addFilm(Film film) {
        log.debug("Adding film: " + film.getName());
        Integer filmId = generateId();
        film.setId(filmId);
        storage.put(filmId, film);
        log.debug("Film added: " + film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws FilmNotFoundException {
        log.debug("Updating film: " + film.getName());
        if (storage.containsKey(film.getId())) {
            storage.put(film.getId(), film);
            log.debug("Film " + film.getName() + " has been updated");
            return film;
        } else {
            log.warn("Film was not found");
            throw new FilmNotFoundException("Incorrect film details");
        }
    }

    @Override
    public Film deleteFilm(Integer id) {
        log.debug("Deleting film. Id: " + id);
        if (storage.containsKey(id)) {
            log.debug("Film id " + id + " has been deleted");
            return storage.remove(id);
        } else {
            log.warn("Film id " + id + " was not found");
            throw new FilmNotFoundException("Incorrect id");
        }
    }

    @Override
    public Film getFilm(Integer id) throws FilmNotFoundException {
        log.debug("Getting film. Id: " + id);
        if (storage.containsKey(id)) {
            log.debug("Film id " + id + " has been found");
            return storage.get(id);
        } else {
            log.warn("Film id " + id + " was not found");
            throw new FilmNotFoundException("Incorrect id");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.debug("Getting all films");
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteAllFilms() {
        log.debug("Deleting all films");
        storage.clear();
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        log.debug("User {} adding like to film {}", userId, filmId);
        Film film = getFilm(filmId);
        User user = uStorage.getUser(userId);
        film.getLikes().add(user.getId());
        updateFilm(film);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        log.debug("User {} deleting like from film {}", userId, filmId);
        Film film = getFilm(filmId);
        User user = uStorage.getUser(userId);
        film.getLikes().remove(user.getId());
        updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(Integer filmCount) {
        log.debug("Requesting the most popular film of {} count", filmCount);
        return getAllFilms().stream()
                .sorted((film_1, film_2) -> film_2.getLikes().size() - film_1.getLikes().size())
                .limit(filmCount)
                .toList();
    }

    private Integer generateId() {
        return ++id;
    }
}
