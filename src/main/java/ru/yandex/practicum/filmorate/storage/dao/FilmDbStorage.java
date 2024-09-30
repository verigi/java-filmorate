package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.*;

@Slf4j
@Repository
@Primary
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    private static final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .build();
    };

    @Override
    public Film addFilm(Film film) {
        validateMpaAndGenres(film);

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilm(film.getId()); // Проверяем, существует ли фильм
        validateMpaAndGenres(film);

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());

        updateFilmGenres(film);
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                Film film = filmRowMapper.mapRow(rs, rs.getRow());
                film.setGenres(new LinkedHashSet<>(extractGenres(id)));
                film.setLikes(extractLikes(id));
                return film;
            } else {
                log.warn("Film with id {} was not found", id);
                throw new FilmNotFoundException("Film with ID " + id + " not found.");
            }
        });
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        films.forEach(film -> {
            film.setGenres(new LinkedHashSet<>(extractGenres(film.getId())));
            film.setLikes(extractLikes(film.getId()));
        });
        return films;
    }

    @Override
    public Film deleteFilmById(Integer id) {
        Film film = getFilm(id);  // Проверяем, существует ли фильм
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
        return film;
    }

    @Override
    public void deleteAllFilms() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        getFilm(filmId); // Проверяем фильм
        userDbStorage.getUser(userId); // Проверяем пользователя
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        getFilm(filmId); // Проверяем фильм
        userDbStorage.getUser(userId); // Проверяем пользователя
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer filmCount) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                "COUNT(fl.user_id) AS likes_count " +
                "FROM films f LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, filmRowMapper, filmCount);
    }

    private void updateFilmGenres(Film film) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().forEach(genre -> jdbcTemplate.update(insertSql, film.getId(), genre.getId()));
        }
    }

    private Set<Integer> extractLikes(Integer filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, filmId));
    }

    private Set<Genre> extractGenres(Integer filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genre g " +
                "JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")), filmId));
    }

    private void validateMpaAndGenres(Film film) {
        mpaDbStorage.getMpa(film.getMpa().getId())
                .orElseThrow(() -> {
                    log.debug("MPA with id {} not found", film.getMpa().getId());
                    throw new ValidationException("Invalid MPA id: " + film.getMpa().getId());
                });

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> genreDbStorage.getGenre(genre.getId())
                    .orElseThrow(() -> {
                        log.debug("Genre with id {} not found", genre.getId());
                        throw new ValidationException("Invalid genre id: " + genre.getId());
                    }));
        }
    }
}
