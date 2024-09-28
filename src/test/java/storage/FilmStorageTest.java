package storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.inmemory.InMemoryFilmStorage;

import java.util.Collections;
import java.util.List;

public class FilmStorageTest extends StorageTest {
    InMemoryFilmStorage service = new InMemoryFilmStorage();

    @BeforeEach
    @Override
    public void init() {
        service.deleteAllFilms();
        service.addFilm(film1);
        service.addFilm(film2);
    }

    @Test
    @DisplayName("Adding film to film storage")
    public void shouldAddFilmToFilmStorage() {
        service.addFilm(film3);
        Assertions.assertEquals(3, service.getAllFilms().size());
    }

    @Test
    @DisplayName("Updating existing film")
    public void shouldUpdateDetailsOfFilm() {
        film1.setName("Test_Film_1_Upd");
        service.updateFilm(film1);
        Assertions.assertEquals("Test_Film_1_Upd", film1.getName());
    }

    @Test
    @DisplayName("Deleting film from film storage")
    public void shouldDeleteFilmFromFilmStorage() {
        service.deleteFilmById(1);
        Assertions.assertEquals(1, service.getAllFilms().size());
        Assertions.assertEquals(List.of(film2), service.getAllFilms());
    }

    @Test
    @DisplayName("Getting film from film storage")
    public void shouldReturnCertainFilmFromStorage() {
        Assertions.assertEquals(film1, service.getFilm(1));
    }

    @Test
    @DisplayName("Clearing film storage")
    public void shouldCleanFilmStorage() {
        service.addFilm(film3);
        service.addFilm(film4);
        service.deleteAllFilms();
        Assertions.assertEquals(Collections.EMPTY_LIST, service.getAllFilms());
    }

}
