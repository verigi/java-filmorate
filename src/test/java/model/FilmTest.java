package model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

public class FilmTest {
    private Film defaultFilm = Film.builder()
            .name("Film test")
            .description("Film test description")
            .releaseDate(LocalDate.of(2000, 1, 1))
            .duration(60)
            .build();
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("Creating new film")
    public void shouldCreateNewFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(defaultFilm);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Null name check")
    public void shouldNotCreateNewFilmNullName() {
        Film film = new Film(null, "Some description",
                LocalDate.of(2000, 1, 1), 60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertThrows(NullPointerException.class, () -> defaultFilm.setName(null));
    }

    @Test
    @DisplayName("Empty name check")
    public void shouldNotCreateNewFilmEmptyName() {
        Film film = new Film("", "Some description",
                LocalDate.of(2000, 1, 1), 60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertThrows(NullPointerException.class, () -> defaultFilm.setName(null));
    }

    @Test
    @DisplayName("Long description check")
    public void shouldNotCreateNewFilmWithLongDescription() {
        char[] charArr = new char[201];
        Arrays.fill(charArr, '*');
        String longDescription = new String(charArr);
        Film film = new Film("Test", longDescription,
                LocalDate.of(2000, 1, 1), 60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals("Description should be less than 200 symbols",
                violations.iterator().next().getMessage());
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Duration check")
    public void shouldNotCreateNewFilmWithUnpositiveDuration() {
        Film film = new Film("Test", "Test description",
                LocalDate.of(2000, 1, 1), 0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
    }
}
