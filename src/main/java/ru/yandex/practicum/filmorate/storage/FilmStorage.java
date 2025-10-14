package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();
    Film findById(Long id);
    Film save(Film film);
    Film update(Film film);
    void delete(Long id);
    void addLike(Long filmId, Long userId);
    void removeLike(Long filmId, Long userId);
    List<Film> getPopularFilms(int count);

    // Добавляем методы для тестов
    default Optional<Film> getFilmById(Long id) {
        Film film = findById(id);
        return film != null ? Optional.of(film) : Optional.empty();
    }

    default boolean filmExists(Long id) {
        return findById(id) != null;
    }
}
