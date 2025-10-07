package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    void getAllFilmsShouldReturnEmptyListInitially() {
        List<Film> films = filmStorage.getAllFilms();
        assertNotNull(films);
        assertTrue(films.isEmpty());
    }

    @Test
    void createFilmShouldAssignIdAndAddToStorage() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmStorage.createFilm(film);

        assertNotNull(createdFilm);
        assertTrue(createdFilm.getId() > 0);
        assertEquals("Test Film", createdFilm.getName());

        List<Film> films = filmStorage.getAllFilms();
        assertEquals(1, films.size());
        assertEquals(createdFilm.getId(), films.get(0).getId());
    }

    @Test
    void updateFilmShouldReplaceExistingFilm() {
        // Create film first
        Film film = new Film();
        film.setName("Original Film");
        film.setDescription("Original Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmStorage.createFilm(film);

        // Update film
        Film updatedFilm = new Film();
        updatedFilm.setId(createdFilm.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(150);

        Film result = filmStorage.updateFilm(updatedFilm);

        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(150, result.getDuration());

        Optional<Film> retrievedFilm = filmStorage.getFilmById(createdFilm.getId());
        assertTrue(retrievedFilm.isPresent());
        assertEquals("Updated Film", retrievedFilm.get().getName());
    }

    @Test
    void getFilmByIdShouldReturnFilmWhenExists() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmStorage.createFilm(film);

        Optional<Film> retrievedFilm = filmStorage.getFilmById(createdFilm.getId());

        assertTrue(retrievedFilm.isPresent());
        assertEquals(createdFilm.getId(), retrievedFilm.get().getId());
        assertEquals("Test Film", retrievedFilm.get().getName());
    }

    @Test
    void getFilmByIdShouldReturnEmptyWhenNotExists() {
        Optional<Film> film = filmStorage.getFilmById(9999);
        assertFalse(film.isPresent());
    }

    @Test
    void deleteFilmShouldRemoveFilmFromStorage() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmStorage.createFilm(film);

        // Verify film exists
        assertTrue(filmStorage.filmExists(createdFilm.getId()));

        // Delete film
        filmStorage.deleteFilm(createdFilm.getId());

        // Verify film no longer exists
        assertFalse(filmStorage.filmExists(createdFilm.getId()));
        Optional<Film> deletedFilm = filmStorage.getFilmById(createdFilm.getId());
        assertFalse(deletedFilm.isPresent());
    }

    @Test
    void filmExistsShouldReturnTrueForExistingFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmStorage.createFilm(film);

        assertTrue(filmStorage.filmExists(createdFilm.getId()));
    }

    @Test
    void filmExistsShouldReturnFalseForNonExistingFilm() {
        assertFalse(filmStorage.filmExists(9999));
    }

    @Test
    void multipleFilmsShouldBeStoredCorrectly() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(150);

        Film createdFilm1 = filmStorage.createFilm(film1);
        Film createdFilm2 = filmStorage.createFilm(film2);

        List<Film> films = filmStorage.getAllFilms();
        assertEquals(2, films.size());

        assertTrue(films.stream().anyMatch(f -> f.getId() == createdFilm1.getId()));
        assertTrue(films.stream().anyMatch(f -> f.getId() == createdFilm2.getId()));
    }
}
