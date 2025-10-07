package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class, GenreDbStorage.class, MpaDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private MpaDbStorage mpaStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);

        Mpa mpa = new Mpa(1, "G");
        testFilm.setMpa(mpa);
    }

    @Test
    void testCreateAndGetFilm() {
        Film createdFilm = filmStorage.createFilm(testFilm);

        Optional<Film> retrievedFilm = filmStorage.getFilmById(createdFilm.getId());

        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getName()).isEqualTo("Test Film");
        assertThat(retrievedFilm.get().getDescription()).isEqualTo("Test Description");
        assertThat(retrievedFilm.get().getMpa().getId()).isEqualTo(1);
        assertThat(retrievedFilm.get().getMpa().getName()).isEqualTo("G");
    }

    @Test
    void testUpdateFilm() {
        Film createdFilm = filmStorage.createFilm(testFilm);

        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");
        createdFilm.getMpa().setId(2); // PG

        Film updatedFilm = filmStorage.updateFilm(createdFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedFilm.getMpa().getId()).isEqualTo(2);
    }

    @Test
    void testGetAllFilms() {
        filmStorage.createFilm(testFilm);

        Film anotherFilm = new Film();
        anotherFilm.setName("Another Film");
        anotherFilm.setDescription("Another Description");
        anotherFilm.setReleaseDate(LocalDate.of(2010, 1, 1));
        anotherFilm.setDuration(90);
        anotherFilm.setMpa(new Mpa(3, "PG-13"));
        filmStorage.createFilm(anotherFilm);

        List<Film> films = filmStorage.getAllFilms();

        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getName)
                .containsExactlyInAnyOrder("Test Film", "Another Film");
    }

    @Test
    void testFilmWithGenres() {
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(2, "Драма");
        testFilm.setGenres(Arrays.asList(genre1, genre2));

        Film createdFilm = filmStorage.createFilm(testFilm);

        Optional<Film> retrievedFilm = filmStorage.getFilmById(createdFilm.getId());

        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getGenres()).hasSize(2);
        assertThat(retrievedFilm.get().getGenres())
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void testFilmExists() {
        Film createdFilm = filmStorage.createFilm(testFilm);

        boolean exists = filmStorage.filmExists(createdFilm.getId());

        assertThat(exists).isTrue();
        assertThat(filmStorage.filmExists(9999)).isFalse();
    }
}
