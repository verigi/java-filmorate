package db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbTest {
    private final FilmService service;
    private final Mpa mpa = Mpa.builder()
            .id(1)
            .name("G")
            .build();
    private final Film film = Film.builder()
            .name("Test_Film_1")
            .description("Description")
            .releaseDate(LocalDate.of(2000, 1, 1))
            .duration(120)
            .mpa(mpa)
            .build();

    private final Film film2 = Film.builder()
            .name("Test_Film_2")
            .description("Description")
            .releaseDate(LocalDate.of(2000, 1, 1))
            .duration(120)
            .mpa(mpa)
            .build();


    @Test
    @BeforeEach
    void clear() {
        service.deleteAllFilms();
    }

    @Test
    @DisplayName("Add film")
    void shouldAddFilmToDb() {
        Film addedFilm = service.addFilm(film);

        assertNotNull(addedFilm.getId());
        assertEquals("Test_Film_1", addedFilm.getName());
        assertEquals("Description", addedFilm.getDescription());
    }

    @Test
    @DisplayName("Update film")
    void shouldUpdateFilm() {
        Film addedFilm = service.addFilm(film);

        Film updFilm = Film.builder()
                .id(addedFilm.getId())
                .name("Upd_Test_Film")
                .description("Upd_Description")
                .releaseDate(LocalDate.of(2000, 2, 2))
                .duration(130)
                .mpa(new Mpa(2, "PG"))
                .build();

        service.updateFilm(updFilm);
        assertEquals(updFilm.getName(), service.getFilm(1).getName());
        assertEquals(updFilm.getDescription(), service.getFilm(1).getDescription());
        assertEquals(updFilm.getReleaseDate(), service.getFilm(1).getReleaseDate());
        assertEquals(updFilm.getDuration(), service.getFilm(1).getDuration());
        assertEquals(updFilm.getMpa(), service.getFilm(1).getMpa());
    }

    @Test
    @DisplayName("Get film")
    void shouldReturnCorrectFilm() {
        Film filmAdded = service.addFilm(film);
        Film filmGot = service.getFilm(7);
        assertEquals(filmAdded.getName(), filmGot.getName());
        assertEquals(filmAdded.getDescription(), filmGot.getDescription());
        assertEquals(filmAdded.getReleaseDate(), filmGot.getReleaseDate());
        assertEquals(filmAdded.getDuration(), filmGot.getDuration());
        assertEquals(filmAdded.getMpa(), filmGot.getMpa());
    }

    @Test
    @DisplayName("Get all films")
    void shouldReturnAllFilms() {
        service.addFilm(film);
        service.addFilm(film2);
        assertEquals(service.getAllFilms().size(), 2);
    }

    @Test
    @DisplayName("Delete film")
    void shouldDeleteFilm() {
        service.addFilm(film);
        service.addFilm(film2);
        service.deleteFilm(8);
        assertEquals(service.getAllFilms().size(), 1);
    }

    @Test
    @DisplayName("Delete all film")
    void shouldDeleteAllFilms() {
        service.addFilm(film);
        service.addFilm(film2);
        service.deleteAllFilms();
        assertEquals(service.getAllFilms().size(), 0);
    }
}
