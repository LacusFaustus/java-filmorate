package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.filmExists(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public void addLike(int filmId, int userId) {
        if (!filmStorage.filmExists(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        if (!userStorage.userExists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        if (!filmStorage.filmExists(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        if (!userStorage.userExists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше " + MIN_RELEASE_DATE);
        }
        if (film.getMpa() == null) {
            throw new ValidationException("Рейтинг MPA не может быть пустым");
        }
    }
}
