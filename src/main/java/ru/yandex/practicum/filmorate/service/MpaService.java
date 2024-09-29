package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class MpaService {
    private final MpaStorage storage;

    public Collection<Mpa> getAllMpa() {
        log.debug("Requesting all mpa");
        return storage.getAllMpa();
    }

    public Mpa getMpa(Integer id) {
        log.debug("Getting mpa with id {}", id);
        return storage.getMpa(id).get();
    }
}
