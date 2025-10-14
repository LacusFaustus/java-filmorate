package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
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

    private FilmDto createValidFilmDto() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Test Film");
        filmDto.setDescription("Test Description");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);
        return filmDto;
    }

    private Film createValidFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));
        return film;
    }

    @Test
    void createFilmWithValidData() {
        FilmDto filmDto = createValidFilmDto();
        Film film = createValidFilm();

        // Мокаем преобразование DTO в модель и обратно
        when(filmService.createFilm(any(Film.class))).thenReturn(film);

        FilmDto result = filmController.createFilm(filmDto);
        assertNotNull(result);
        assertEquals("Test Film", result.getName());
        verify(filmService, times(1)).createFilm(any(Film.class));
    }

    @Test
    void createFilmWithEarlyReleaseDate() {
        FilmDto filmDto = createValidFilmDto();
        filmDto.setReleaseDate(LocalDate.of(1890, 1, 1));

        when(filmService.createFilm(any(Film.class))).thenThrow(
                new ru.yandex.practicum.filmorate.exception.ValidationException("Дата релиза не может быть раньше 1895-12-28")
        );

        assertThrows(ru.yandex.practicum.filmorate.exception.ValidationException.class,
                () -> filmController.createFilm(filmDto));
        verify(filmService, times(1)).createFilm(any(Film.class));
    }

    @Test
    void updateFilmWithValidData() {
        FilmDto filmDto = createValidFilmDto();
        filmDto.setId(1L);
        Film film = createValidFilm();

        when(filmService.updateFilm(any(Film.class))).thenReturn(film);

        FilmDto result = filmController.updateFilm(filmDto);
        assertNotNull(result);
        assertEquals("Test Film", result.getName());
        verify(filmService, times(1)).updateFilm(any(Film.class));
    }

    @Test
    void getFilmById() {
        Film film = createValidFilm();

        when(filmService.getFilmById(1L)).thenReturn(film);

        FilmDto result = filmController.getFilmById(1L);
        assertNotNull(result);
        assertEquals("Test Film", result.getName());
        verify(filmService, times(1)).getFilmById(1L);
    }
}
