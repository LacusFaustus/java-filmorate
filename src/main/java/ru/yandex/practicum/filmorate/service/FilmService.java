package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getAllFilms() {
        log.debug("Получение всех фильмов из хранилища");
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        log.debug("Создание нового фильма: {}", film);
        validateFilm(film);
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Создан новый фильм с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        log.debug("Обновление фильма: {}", film);

        if (!filmStorage.filmExists(film.getId())) {
            log.warn("Попытка обновления несуществующего фильма с ID: {}", film.getId());
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        validateFilm(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Обновлен фильм с ID: {}", updatedFilm.getId());
        return updatedFilm;
    }

    public Film getFilmById(int id) {
        log.debug("Получение фильма по ID: {}", id);
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> {
                    log.warn("Фильм с ID {} не найден", id);
                    return new NotFoundException("Фильм с id=" + id + " не найден");
                });
    }

    public void addLike(int filmId, int userId) {
        log.debug("Добавление лайка фильму {} от пользователя {}", filmId, userId);
        Film film = getFilmById(filmId);
        film.addLike(userId);
        filmStorage.updateFilm(film);
        log.info("Добавлен лайк фильму {} от пользователя {}", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        log.debug("Удаление лайка у фильма {} от пользователя {}", filmId, userId);
        Film film = getFilmById(filmId);
        film.removeLike(userId);
        filmStorage.updateFilm(film);
        log.info("Удален лайк у фильма {} от пользователя {}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Получение {} популярных фильмов", count);
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        log.debug("Валидация фильма: {}", film);

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Попытка создания/обновления фильма с неверной датой релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше " + MIN_RELEASE_DATE);
        }

        log.debug("Валидация фильма прошла успешно");
    }
}
