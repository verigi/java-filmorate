package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage,
                       GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film addFilm(Film film) {
        log.debug("Adding film {} to database", film.getName());
        validateMpaAndGenres(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.debug("Updating film with id {}", film.getId());
        validateMpaAndGenres(film);
        return filmStorage.updateFilm(film);
    }

    public Film deleteFilm(Integer id) {
        log.debug("Deleting film with id {} from database", id);
        return filmStorage.deleteFilm(id);
    }

    public Film getFilm(Integer id) {
        log.debug("Getting film with id {} from database", id);
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

    // Работа с лайками
    public void addLike(Integer filmId, Integer userId) {
        log.debug("User {} adding like to film {}", userId, filmId);
        userStorage.getUser(userId);
        filmStorage.addLike(filmId, userId);
        log.debug("Like added successfully");
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.debug("User {} deleting like from film {}", userId, filmId);
        userStorage.getUser(userId);
        filmStorage.deleteLike(filmId, userId);
        log.debug("Like deleted successfully");
    }

    public List<Film> getPopularFilms(Integer filmCount) {
        log.debug("Requesting the most popular films with a count of {}", filmCount);
        return filmStorage.getPopularFilms(filmCount);
    }

    private void validateMpaAndGenres(Film film) {
        mpaStorage.getMpa(film.getMpa().getId())
                .orElseThrow(() -> {
                    log.debug("MPA with id {} not found", film.getMpa().getId());
                    throw new ValidationException("Incorrect MPA id: " + film.getMpa().getId());
                });

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> genreStorage.getGenre(genre.getId())
                    .orElseThrow(() -> {
                        log.debug("Genre with id {} not found", genre.getId());
                        throw new ValidationException("Incorrect genre id: " + genre.getId());
                    }));
        }
    }
}

