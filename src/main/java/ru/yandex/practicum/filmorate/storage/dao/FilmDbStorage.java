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

    private static final RowMapper<Film> filmRowMapper = (res, rowNum) -> Film.builder()
            .id(res.getInt("film_id"))
            .name(res.getString("name"))
            .description(res.getString("description"))
            .releaseDate(res.getDate("release_date").toLocalDate())
            .duration(res.getInt("duration"))
            .mpa(new Mpa(res.getInt("mpa_id"), res.getString("mpa_name")))
            .build();

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

        film.setId(keyHolder.getKey().intValue());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?," +
                " mpa_id = ? WHERE film_id = ?";

        getFilm(film.getId());
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films AS f JOIN mpa AS m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";

        Film film = jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            if (rs.next()) {
                return filmRowMapper.mapRow(rs, rs.getRow());
            } else {
                log.warn("Film with id {} was not found", id);
                throw new FilmNotFoundException("Film with ID " + id + " not found.");
            }
        });

        film.setGenres(extractGenres().getOrDefault(film.getId(), new LinkedHashSet<>()));//если нет знач. по id нет, пустой
        film.setLikes(extractLikes().getOrDefault(film.getId(), new HashSet<>()));//если нет знач. по id нет, пустой

        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date," +
                " f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        films.forEach(film -> {
            film.setGenres(extractGenres().getOrDefault(film.getId(), new LinkedHashSet<>()));
            film.setLikes(extractLikes().getOrDefault(film.getId(), new HashSet<>()));
        });

        return films;
    }

    @Override
    public Film deleteFilm(Integer id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        Film film = getFilm(id);
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
        getFilm(filmId);
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        getFilm(filmId);
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer filmCount) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "m.name AS mpa_name, COUNT(fl.user_id) AS likes_count " +
                "FROM films AS f LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                "ORDER BY likes_count DESC LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, filmCount);

        films.forEach(film -> {
            film.setGenres(extractGenres().getOrDefault(film.getId(), new LinkedHashSet<>()));
            film.setLikes(extractLikes().getOrDefault(film.getId(), new HashSet<>()));
        });

        return films;
    }

    private void updateFilmGenres(Film film) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().forEach(genre -> jdbcTemplate.update(insertSql, film.getId(), genre.getId()));
        }
    }

    private Map<Integer, Set<Integer>> extractLikes() {
        String sql = "SELECT fl.film_id, fl.user_id FROM film_likes AS fl";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Set<Integer>> likesMap = new HashMap<>();
            while (rs.next()) {
                int filmId = rs.getInt("film_id");
                int userId = rs.getInt("user_id");
                likesMap.computeIfAbsent(filmId, userLike -> new HashSet<>()).add(userId);
            }
            return likesMap;
        });
    }

    private Map<Integer, LinkedHashSet<Genre>> extractGenres() {
        String sql = "SELECT fg.film_id, g.genre_id, g.name " +
                "FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id = g.genre_id";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, LinkedHashSet<Genre>> genreMap = new HashMap<>();
            while (rs.next()) {
                int filmId = rs.getInt("film_id");
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
                genreMap.computeIfAbsent(filmId, genreId -> new LinkedHashSet<>()).add(genre);// если нет жанров, пустой
            }
            return genreMap;
        });
    }
}