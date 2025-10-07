package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FilmServiceTest {
    @Mock
    private FilmStorage filmStorage;

    @InjectMocks
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFilmByIdWhenFilmExists() {
        Film film = new Film();
        film.setId(1);
        film.setName("Test Film");

        when(filmStorage.getFilmById(1)).thenReturn(Optional.of(film));

        Film result = filmService.getFilmById(1);
        assertNotNull(result);
        assertEquals("Test Film", result.getName());
    }

    @Test
    void getFilmByIdWhenFilmNotExists() {
        when(filmStorage.getFilmById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.getFilmById(1));
    }
}
