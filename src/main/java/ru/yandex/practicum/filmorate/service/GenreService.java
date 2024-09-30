package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage storage;

    @Autowired
    public GenreService(GenreStorage storage) {
        this.storage = storage;
    }

    public Collection<Genre> getAllGenres() {
        log.debug("Requesting all genres");
        return storage.getAllGenres();
    }

    public Genre getGenre(Integer id) {
        log.debug("Getting genre with id {}", id);
        return storage.getGenre(id).orElseThrow(() -> new GenreNotFoundException("Incorrect genre id: " + id));
    }

}
