package storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserService;

import java.time.Duration;
import java.time.LocalDate;

public abstract class StorageTest {

    protected Film film_1 = Film.builder()
            .name("Test_Film_1")
            .description("Some_Description_1")
            .releaseDate(LocalDate.of(2000, 01, 01))
            .duration(60)
            .build();

    protected Film film_2 = Film.builder()
            .name("Test_Film_2")
            .description("Some_Description_2")
            .releaseDate(LocalDate.of(2000, 02, 02))
            .duration(60)
            .build();

    protected User user_1 = User.builder()
            .email("test_1@yandex.ru")
            .login("test_login_1")
            .name("test_name_1")
            .birthday(LocalDate.of(2000, 01, 01))
            .build();

    protected User user_2 = User.builder()
            .email("test_2@yandex.ru")
            .login("test_login_2")
            .name("test_name_2")
            .birthday(LocalDate.of(2000, 02, 02))
            .build();

    protected User user_3 = User.builder()
            .email("test_3@yandex.ru")
            .login("test_login_3")
            .name("test_name_3")
            .birthday(LocalDate.of(2000, 03, 03))
            .build();

    protected User user_4 = User.builder()
            .email("test_4@yandex.ru")
            .login("test_login_4")
            .name("")
            .birthday(LocalDate.of(2000, 03, 03))
            .build();


    protected abstract void init();
}
