package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Collection<Mpa> getAllMpa();

    Optional<Mpa> getMpaById(Integer id);
}
