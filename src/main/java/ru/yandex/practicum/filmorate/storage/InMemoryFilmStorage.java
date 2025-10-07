package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(nextId.getAndIncrement());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void deleteFilm(int id) {
        films.remove(id);
    }

    @Override
    public boolean filmExists(int id) {
        return films.containsKey(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.addLike(userId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
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
}
