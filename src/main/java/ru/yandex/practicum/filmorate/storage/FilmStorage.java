package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAllFilms();
    Film createFilm(Film film);
    Film updateFilm(Film film);
    Optional<Film> getFilmById(int id);
    void deleteFilm(int id);
    boolean filmExists(int id);
    void addLike(int filmId, int userId);
    void removeLike(int filmId, int userId);
    List<Film> getPopularFilms(int count);
}
