package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FilmControllerTest {
    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFilmWithValidData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        when(filmService.createFilm(any(Film.class))).thenReturn(film);

        Film result = filmController.createFilm(film);
        assertNotNull(result);
        assertEquals("Test Film", result.getName());
        verify(filmService, times(1)).createFilm(film);
    }

    @Test
    void createFilmWithEarlyReleaseDate() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(120);

        // Теперь валидация происходит в сервисе, поэтому контроллер не должен бросать исключение
        // Вместо этого сервис бросит ValidationException при вызове createFilm
        when(filmService.createFilm(any(Film.class))).thenThrow(
                new ru.yandex.practicum.filmorate.exception.ValidationException("Дата релиза не может быть раньше 1895-12-28")
        );

        assertThrows(ru.yandex.practicum.filmorate.exception.ValidationException.class,
                () -> filmController.createFilm(film));
        verify(filmService, times(1)).createFilm(film);
    }
}
