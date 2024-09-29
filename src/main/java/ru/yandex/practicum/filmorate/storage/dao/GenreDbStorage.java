package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Repository
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Genre> genreRowMapper = (ResultSet res, int rowNum) -> Genre.builder()
            .id(res.getInt("genre_id"))
            .name(res.getString("name"))
            .build();

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Optional<Genre> getGenre(Integer id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        return jdbcTemplate.query(sql, genreRowMapper, id)
                .stream()
                .findFirst();
    }
}
