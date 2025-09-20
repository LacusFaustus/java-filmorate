package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createFilmWithValidDataShouldReturnCreated() {
        Film film = new Film();
        film.setName("The Matrix");
        film.setDescription("A computer hacker learns from mysterious rebels about the true nature of his reality");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Matrix", response.getBody().getName());
        assertTrue(response.getBody().getId() > 0);
    }

    @Test
    void createFilmWithEmptyNameShouldReturnBadRequest() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmWithEarlyReleaseDateShouldReturnBadRequest() {
        Film film = new Film();
        film.setName("Early Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmWithNegativeDurationShouldReturnBadRequest() {
        Film film = new Film();
        film.setName("Negative Duration Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateFilmWithValidDataShouldReturnOk() {
        // First create a film
        Film film = new Film();
        film.setName("Original Film");
        film.setDescription("Original Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<Film> createResponse = restTemplate.postForEntity("/films", film, Film.class);
        Film createdFilm = createResponse.getBody();

        // Then update it
        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");

        ResponseEntity<Film> updateResponse = restTemplate.exchange(
                "/films",
                HttpMethod.PUT,
                new HttpEntity<>(createdFilm),
                Film.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("Updated Film", updateResponse.getBody().getName());
    }

    @Test
    void updateNonExistentFilmShouldReturnNotFound() {
        Film film = new Film();
        film.setId(9999);
        film.setName("Non-existent Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.exchange(
                "/films",
                HttpMethod.PUT,
                new HttpEntity<>(film),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFilmByIdShouldReturnFilm() {
        // First create a film
        Film film = new Film();
        film.setName("Film for Get Test");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ResponseEntity<Film> createResponse = restTemplate.postForEntity("/films", film, Film.class);
        Film createdFilm = createResponse.getBody();

        // Then get it by ID
        ResponseEntity<Film> getResponse = restTemplate.getForEntity(
                "/films/" + createdFilm.getId(),
                Film.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("Film for Get Test", getResponse.getBody().getName());
    }

    @Test
    void getNonExistentFilmShouldReturnNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/films/9999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
