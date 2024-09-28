package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film addFilm(Film film) {
        log.debug("Adding film {} to database", film.getName());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.debug("Updating film {} to database", film.getName());
        return filmStorage.updateFilm(film);
    }

    public Film deleteFilm(Integer id) {
        log.debug("Deleting film {} from database", filmStorage.getFilm(id).getName());
        return filmStorage.deleteFilmById(id);
    }

    public Film getFilm(Integer id) {
        log.debug("Getting film {} from database", filmStorage.getFilm(id).getName());
        return filmStorage.getFilm(id);
    }

    public Collection<Film> getAllFilms() {
        log.debug("Getting list of films");
        return filmStorage.getAllFilms();
    }

    public void deleteAllFilms() {
        log.debug("Delete all films from database");
        filmStorage.deleteAllFilms();
        log.debug("Delete successful");
    }

    // работа с лайками
    public void addLike(Integer filmId, Integer userId) {
        log.debug("User {} adding like to film {}", userId, filmId);
        filmStorage.addLike(filmId, userId);
        log.debug("Like added successfully");
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.debug("User {} deleting like from film {}", userId, filmId);
        filmStorage.deleteLike(filmId, userId);
        log.debug("Like deleted successfully");
    }

    public List<Film> getPopularFilms(Integer filmCount) {
        log.debug("Requesting the most popular film of {} count", filmCount);
        return filmStorage.getPopularFilms(filmCount);
    }

    // работа с жанрами
    public Collection<Genre> getAllGenres() {
        log.debug("Requesting all genres");
        return genreStorage.getAllGenres();
    }

    public Genre getGenre(Integer id) {
        log.debug("Getting genre with id {}", id);
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre with id " + id + " does not exist"));
    }


    // работа с рейтингами
    public Collection<Mpa> getAllMpa() {
        log.debug("Requesting all mpa");
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpa(Integer id) {
        log.debug("Getting mpa with id {}", id);
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new MpaNotFoundException("Mpa with id " + id + " does not exist"));
    }
}
