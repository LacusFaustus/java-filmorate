package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa_ratings m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        loadGenresForFilms(films);
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_id", film.getMpa().getId());

        Number key = simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId(key.intValue());

        saveFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateFilmGenres(film);
        return getFilmById(film.getId()).orElse(film);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.get(0);
        loadGenresForFilm(film);
        return Optional.of(film);
    }

    @Override
    public void deleteFilm(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean filmExists(int id) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name as mpa_name, COUNT(fl.user_id) as likes_count " +
                "FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_id = m.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id, m.name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        loadGenresForFilms(films);
        return films;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            batchArgs.add(new Object[]{film.getId(), genre.getId()});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void updateFilmGenres(Film film) {
        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());
        saveFilmGenres(film);
    }

    private void loadGenresForFilm(Film film) {
        String sql = "SELECT g.id, g.name FROM film_genres fg JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(genres);
    }

    private void loadGenresForFilms(List<Film> films) {
        for (Film film : films) {
            loadGenresForFilm(film);
        }
    }
}
