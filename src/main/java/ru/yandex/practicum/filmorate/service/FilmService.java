package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    public Film createFilm(Film film) {
        validateFilm(film);

        // Убедимся, что MPA установлен
        if (film.getMpa() == null) {
            throw new IllegalArgumentException("MPA rating is required");
        }

        return filmStorage.save(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);

        // Убедимся, что MPA установлен
        if (film.getMpa() == null) {
            throw new IllegalArgumentException("MPA rating is required");
        }

        Film updatedFilm = filmStorage.update(film);
        if (updatedFilm == null) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        return updatedFilm;
    }

    public void deleteFilm(Long id) {
        filmStorage.delete(id);
    }

    public void addLike(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getPopularFilms(int count, int page) {
        List<Film> popularFilms = filmStorage.getPopularFilms(count * page);
        int fromIndex = (page - 1) * count;
        if (fromIndex >= popularFilms.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + count, popularFilms.size());
        return popularFilms.subList(fromIndex, toIndex);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateFilmAndUser(Long filmId, Long userId) {
        if (filmStorage.findById(filmId) == null) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }
}
