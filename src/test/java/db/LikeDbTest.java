package db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeDbTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Test
    @DisplayName("Add like")
    void shouldAddLike() {
        User user = User.builder()
                .email("test@ya.ru")
                .login("Test")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User savedUser = userStorage.addUser(user);

        Film film = Film.builder()
                .name("Test_Film_1")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .build();
        Film savedFilm = filmStorage.addFilm(film);
        filmStorage.addLike(savedFilm.getId(), savedUser.getId());

        Film filmWithLikes = filmStorage.getFilm(savedFilm.getId());
        Assertions.assertTrue(filmWithLikes.getLikes().contains(savedUser.getId()));
    }

    @Test
    @DisplayName("Delete like")
    void shouldDeleteLike() {
        User user = User.builder()
                .email("test@ya.ru")
                .login("Test")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        User savedUser = userStorage.addUser(user);

        Film film = Film.builder()
                .name("Test_Film_1")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new Mpa(1, "G"))
                .build();
        Film savedFilm = filmStorage.addFilm(film);
        filmStorage.addLike(savedFilm.getId(), savedUser.getId());

        filmStorage.deleteLike(savedFilm.getId(), savedUser.getId());

        Film filmWithoutLikes = filmStorage.getFilm(savedFilm.getId());
        Assertions.assertFalse(filmWithoutLikes.getLikes().contains(savedUser.getId()));
    }
}
