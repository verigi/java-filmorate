package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Mpa> mpaRowMapper = (ResultSet res, int rowNum) -> Mpa.builder()
            .id(res.getInt("mpa_id"))
            .name(res.getString("name"))
            .build();

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT mpa_id FROM mpa";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public Optional<Mpa> getMpaById(Integer id) {
        String sql = "SELECT mpa_id FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.query(sql, mpaRowMapper, id)
                .stream()
                .findFirst();
    }
}
