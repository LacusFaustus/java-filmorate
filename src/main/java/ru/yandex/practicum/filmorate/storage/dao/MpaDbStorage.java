package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        return mpaList.stream().findFirst();
    }

    @Override
    public boolean mpaExists(int id) {
        String sql = "SELECT COUNT(*) FROM mpa_ratings WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }
}
