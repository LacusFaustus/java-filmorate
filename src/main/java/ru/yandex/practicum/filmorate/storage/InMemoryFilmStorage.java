package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long currentId = 1L;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Long id) {
        return films.get(id);
    }

    @Override
    public Film save(Film film) {
        if (film.getId() == null) {
            film.setId(currentId++);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        films.remove(id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.addLike(userId);
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.removeLike(userId);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());
    }

    // Методы для обратной совместимости с тестами
    public List<Film> getAllFilms() {
        return findAll();
    }

    public Film createFilm(Film film) {
        return save(film);
    }

    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(findById(id));
    }

    public boolean filmExists(Long id) {
        return films.containsKey(id);
    }

    public void deleteFilm(Long id) {
        delete(id);
    }

    public Film updateFilm(Film film) {
        return update(film);
    }
}
