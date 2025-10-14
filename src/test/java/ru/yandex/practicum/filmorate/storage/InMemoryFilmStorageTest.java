package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        List<Film> films = filmStorage.findAll();
        assertNotNull(films);
        assertTrue(films.isEmpty());
    }

    @Test
    void saveFilmShouldAssignIdAndAddToStorage() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film createdFilm = filmStorage.save(film);

        assertNotNull(createdFilm);
        assertTrue(createdFilm.getId() > 0);
        assertEquals("Test Film", createdFilm.getName());

        List<Film> films = filmStorage.findAll();
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
        film.setMpa(new Mpa(1, "G"));

        Film createdFilm = filmStorage.save(film);

        // Update film
        Film updatedFilm = new Film();
        updatedFilm.setId(createdFilm.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(150);
        updatedFilm.setMpa(new Mpa(2, "PG"));

        Film result = filmStorage.update(updatedFilm);

        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(150, result.getDuration());

        Film retrievedFilm = filmStorage.findById(createdFilm.getId());
        assertNotNull(retrievedFilm);
        assertEquals("Updated Film", retrievedFilm.getName());
    }

    @Test
    void findFilmByIdShouldReturnFilmWhenExists() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film createdFilm = filmStorage.save(film);

        Film retrievedFilm = filmStorage.findById(createdFilm.getId());

        assertNotNull(retrievedFilm);
        assertEquals(createdFilm.getId(), retrievedFilm.getId());
        assertEquals("Test Film", retrievedFilm.getName());
    }

    @Test
    void findFilmByIdShouldReturnNullWhenNotExists() {
        Film film = filmStorage.findById(9999L);
        assertNull(film);
    }

    @Test
    void deleteFilmShouldRemoveFilmFromStorage() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film createdFilm = filmStorage.save(film);

        // Verify film exists
        assertTrue(filmStorage.filmExists(createdFilm.getId()));

        // Delete film
        filmStorage.delete(createdFilm.getId());

        // Verify film no longer exists
        assertFalse(filmStorage.filmExists(createdFilm.getId()));
        Film deletedFilm = filmStorage.findById(createdFilm.getId());
        assertNull(deletedFilm);
    }

    @Test
    void filmExistsShouldReturnTrueForExistingFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film createdFilm = filmStorage.save(film);

        assertTrue(filmStorage.filmExists(createdFilm.getId()));
    }

    @Test
    void filmExistsShouldReturnFalseForNonExistingFilm() {
        assertFalse(filmStorage.filmExists(9999L));
    }

    @Test
    void multipleFilmsShouldBeStoredCorrectly() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new Mpa(1, "G"));

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(150);
        film2.setMpa(new Mpa(2, "PG"));

        Film createdFilm1 = filmStorage.save(film1);
        Film createdFilm2 = filmStorage.save(film2);

        List<Film> films = filmStorage.findAll();
        assertEquals(2, films.size());

        assertTrue(films.stream().anyMatch(f -> f.getId().equals(createdFilm1.getId())));
        assertTrue(films.stream().anyMatch(f -> f.getId().equals(createdFilm2.getId())));
    }


}
