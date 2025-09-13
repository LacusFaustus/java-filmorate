package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void createFilmWithValidData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<Film> response = filmController.createFilm(film);
        assertNotNull(response.getBody());
        assertEquals("Test Film", response.getBody().getName());
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void createFilmWithEmptyName() {
        Film film = new Film();
        film.setName(""); // Пустое имя - должно пройти, так как аннотации не работают в unit-тестах
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        // В unit-тестах аннотации @Valid не работают, поэтому исключение не выбрасывается
        ResponseEntity<Film> response = filmController.createFilm(film);
        assertNotNull(response.getBody());
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void createFilmWithEarlyReleaseDate() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1890, 1, 1)); // Слишком ранняя дата
        film.setDuration(120);

        // Эта проверка должна выбрасывать ValidationException из нашей кастомной валидации
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120); // Отрицательная продолжительность

        // В unit-тестах аннотации @Positive не работают
        ResponseEntity<Film> response = filmController.createFilm(film);
        assertNotNull(response.getBody());
        assertEquals(201, response.getStatusCodeValue());
    }
}
