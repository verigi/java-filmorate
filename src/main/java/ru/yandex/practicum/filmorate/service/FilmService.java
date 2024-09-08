package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.utils.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film deleteFilmById(Integer id) {
        return filmStorage.deleteFilmById(id);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().add(user.getId());
        updateFilm(film);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().remove(user.getId());
        updateFilm(film);
    }

    public List<Film> getPopularFilms(Integer filmCount) {
        return filmStorage.getAllFilms().stream()
                .sorted((film_1, film_2) -> film_2.getLikes().size() - film_1.getLikes().size())
                .limit(filmCount)
                .toList();
    }
}
