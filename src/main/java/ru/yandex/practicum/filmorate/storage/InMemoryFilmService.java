package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.utils.FilmInterface;

import java.util.*;

@Slf4j
public class InMemoryFilmService implements FilmInterface {
    private final HashMap<Integer, Film> service = new HashMap<>();
    private Integer id = 0;

    @Override
    public Film addFilm(Film film) {
        log.debug("Adding film: " + film.getName());
        Integer filmId = generateId();
        film.setId(filmId);
        service.put(filmId, film);
        log.debug("Film added: " + film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws FilmNotFoundException {
        log.debug("Updating film: " + film.getName());
        if (!service.get(film.getId()).equals(null)) {
            service.put(film.getId(), film);
            log.debug("Film " + film.getName() + " has been updated");
            return film;
        } else {
            log.warn("Film was not found");
            throw new FilmNotFoundException("Incorrect film details");
        }
    }

    @Override
    public Film deleteFilmById(Integer id) {
        log.debug("Deleting film. Id: " + id);
        if (service.containsKey(id)) {
            log.debug("Film id " + id + " has been deleted");
            return service.remove(id);
        } else {
            log.warn("Film id " + id + " was not found");
            throw new FilmNotFoundException("Incorrect id");
        }
    }

    @Override
    public Film getFilmById(Integer id) throws FilmNotFoundException {
        log.debug("Getting film. Id: " + id);
        if (!service.get(id).equals(null)) {
            log.debug("Film id " + id + " has been found");
            return service.get(id);
        } else {
            log.warn("Film id " + id + " was not found");
            throw new FilmNotFoundException("Incorrect id");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.debug("Getting all films");
        return new ArrayList<>(service.values());
    }

    @Override
    public void deleteAllFilms() {
        log.debug("Deleting all films");
        service.clear();
    }

    private Integer generateId() {
        return ++id;
    }

}
