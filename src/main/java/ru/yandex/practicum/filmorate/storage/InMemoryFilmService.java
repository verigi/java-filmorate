package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.utils.FilmInterface;

import java.util.*;

@Slf4j
public class InMemoryFilmService implements FilmInterface {
    private final HashMap<Integer, Film> FILM_STORAGE = new HashMap<>();
    private Integer id = 0;

    @Override
    public Film addFilm(Film film) {
        log.debug("Adding film: " + film.getName());
        Integer filmId = generateId();
        film.setId(filmId);
        FILM_STORAGE.put(filmId, film);
        log.debug("Film added: " + film.getName());
        return FILM_STORAGE.get(filmId);
    }

    @Override
    public Film updateFilm(Film film) throws FilmNotFoundException {
        log.debug("Updating film: " + film.getName());
        Optional<Film> filmOptional = FILM_STORAGE.keySet().stream()
                .filter(x -> x == film.getId())
                .map(y -> FILM_STORAGE.put(film.getId(), film))
                .findAny();
        if (filmOptional.isPresent()) {
            log.debug("Film " + film.getName() + " has been updated");
            return filmOptional.get();
        } else {
            log.warn("Film was not found");
            throw new FilmNotFoundException("Incorrect film details");
        }
    }

    @Override
    public Film deleteFilmById(Integer id) {
        log.debug("Deleting film. Id: " + id);
        if (FILM_STORAGE.containsKey(id)) {
            log.debug("Film id " + id + " has been deleted");
            return FILM_STORAGE.remove(id);
        } else {
            log.warn("Film id " + id + " was not found");
            throw new FilmNotFoundException("Incorrect id");
        }
    }

    @Override
    public Film getFilmById(Integer id) throws FilmNotFoundException {
        log.debug("Getting film. Id: " + id);
        Optional<Film> filmOptional = FILM_STORAGE.keySet().stream()
                .filter(x -> x == id)
                .map(y -> FILM_STORAGE.get(y))
                .findAny();
        if (filmOptional.isPresent()) {
            log.debug("Film id " + id + " has been found");
            return filmOptional.get();
        } else {
            log.warn("Film id " + id + " was not found");
            throw new FilmNotFoundException("Incorrect id");
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.debug("Getting all films");
        return new ArrayList<>(FILM_STORAGE.values());
    }

    private Integer generateId() {
        return ++id;
    }

}
