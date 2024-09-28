package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Integer id);

    Collection<Film> getAllFilms();

    Film deleteFilmById(Integer id);

    void deleteAllFilms();

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer filmCount);
}
