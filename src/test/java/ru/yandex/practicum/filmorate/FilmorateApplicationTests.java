package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTests {

	private final FilmStorage filmStorage;
	private final JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("Adding film to database")
	void shouldAddFilmToDB() {
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2022, 1, 1));
		film.setDuration(120);
		film.setMpa(new Mpa(1, "G"));

		Film addedFilm = filmStorage.addFilm(film);
		assertNotNull(addedFilm.getId(), "Film ID should not be null after insertion");
		assertEquals(film.getName(), addedFilm.getName(), "Film name should match");
	}

}