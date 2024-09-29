package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
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
    @Autowired
    private UserDbStorage userDbStorage;

    private static final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
        film.setMpa(mpa);
        return film;
    };

    @Override
    public Film addFilm(Film film) {
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
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenresToFilm(film.getId(), film.getGenres());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilm(film.getId());
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());

        if (film.getGenres() != null) {
            addGenresToFilm(film.getId(), film.getGenres());
        }

        film.setGenres(extractGenres(film.getId()));
        film.setLikes(extractLikes(film.getId()));
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                Film film = filmRowMapper.mapRow(rs, rs.getRow());
                film.setGenres(extractGenres(id));
                film.setLikes(extractLikes(id));
                return film;
            } else {
                log.warn("Film id " + id + " was not found");
                throw new FilmNotFoundException("Film with ID " + id + " not found.");
            }
        });
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.*, m.mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id";
        Collection<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        for (Film film : films) {
            film.setLikes(extractLikes(film.getId()));
            film.setGenres(extractGenres(film.getId()));
        }
        return films;
    }

    @Override
    public Film deleteFilmById(Integer id) {
        Film film = getFilm(id);
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
        getFilm(filmId);
        userDbStorage.getUser(userId);
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        getFilm(filmId);
        userDbStorage.getUser(userId);
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer filmCount) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                "COUNT(fl.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, filmRowMapper, filmCount);
    }

    private void addGenresToFilm(Integer filmId, Set<Genre> genres) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);

        if (genres != null && !genres.isEmpty()) {
            String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : genres) {
                jdbcTemplate.update(insertSql, filmId, genre.getId());
            }
        }
    }

    private Set<Integer> extractLikes(Integer filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Integer> userIds = jdbcTemplate.queryForList(sql, new Object[]{filmId}, Integer.class);
        return new HashSet<>(userIds);
    }

    private Set<Genre> extractGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genre g " +
                "JOIN film_genre fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")), filmId);

        return new HashSet<>(genres);
    }
}