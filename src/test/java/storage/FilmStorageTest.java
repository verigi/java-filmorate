package storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.utils.FilmInterface;

import java.util.Collection;

public class FilmStorageTest extends StorageTest implements FilmInterface {

    @Override
    public void init(){

    }

    @Test
    @Override
    public Film addFilm(Film film) {
        return null;
    }
    @Test
    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Test
    @Override
    public Film deleteFilmById(Integer id) {
        return null;
    }

    @Test
    @Override
    public Film getFilmById(Integer id) {
        return null;
    }

    @Test
    @Override
    public Collection<Film> getAllFilms() {
        return null;
    }
}
