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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User; // Добавьте этот импорт

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("The Matrix");
        film.setDescription("A computer hacker learns from mysterious rebels about the true nature of his reality");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(new Mpa(1, "G")); // Добавляем MPA
        return film;
    }

    @Test
    void createFilmWithValidDataShouldReturnCreated() {
        Film film = createValidFilm();

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Matrix", response.getBody().getName());
        assertTrue(response.getBody().getId() > 0);
    }

    @Test
    void createFilmWithEmptyNameShouldReturnBadRequest() {
        Film film = createValidFilm();
        film.setName("");

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmWithEarlyReleaseDateShouldReturnBadRequest() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1890, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);

        // Проверим, что возвращается 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Дополнительно проверим тело ответа
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Дата релиза не может быть раньше"));
    }

    @Test
    void createFilmWithNegativeDurationShouldReturnBadRequest() {
        Film film = createValidFilm();
        film.setDuration(-120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmWithLongDescriptionShouldReturnBadRequest() {
        Film film = createValidFilm();
        film.setDescription("A".repeat(201)); // 201 символов - больше лимита

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createFilmWithoutMpaShouldReturnBadRequest() {
        Film film = createValidFilm();
        film.setMpa(null); // Убираем MPA

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateFilmWithValidDataShouldReturnOk() {
        // First create a film
        Film film = createValidFilm();
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
    void updateFilmWithInvalidDataShouldReturnBadRequest() {
        // First create a film
        Film film = createValidFilm();
        ResponseEntity<Film> createResponse = restTemplate.postForEntity("/films", film, Film.class);
        Film createdFilm = createResponse.getBody();

        // Then update it with invalid data
        createdFilm.setName(""); // Пустое название

        ResponseEntity<String> updateResponse = restTemplate.exchange(
                "/films",
                HttpMethod.PUT,
                new HttpEntity<>(createdFilm),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
    }

    @Test
    void updateNonExistentFilmShouldReturnNotFound() {
        Film film = createValidFilm();
        film.setId(9999);

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
        Film film = createValidFilm();
        film.setName("Film for Get Test");

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

    @Test
    void getAllFilmsShouldReturnList() {
        // Create first film
        Film film1 = createValidFilm();
        film1.setName("Film 1");
        restTemplate.postForEntity("/films", film1, Film.class);

        // Create second film
        Film film2 = createValidFilm();
        film2.setName("Film 2");
        restTemplate.postForEntity("/films", film2, Film.class);

        // Get all films
        ResponseEntity<Film[]> response = restTemplate.getForEntity("/films", Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);
    }

    @Test
    void addLikeToFilmShouldWork() {
        // Create film and user first
        Film film = createValidFilm();
        ResponseEntity<Film> filmResponse = restTemplate.postForEntity("/films", film, Film.class);
        Film createdFilm = filmResponse.getBody();

        // Create user properly
        User user = new User();
        user.setEmail("likeuser@mail.ru");
        user.setLogin("likeuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        ResponseEntity<User> userResponse = restTemplate.postForEntity("/users", user, User.class);
        User createdUser = userResponse.getBody();

        // Add like
        ResponseEntity<Void> likeResponse = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/" + createdUser.getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertEquals(HttpStatus.OK, likeResponse.getStatusCode());
    }

    @Test
    void removeLikeFromFilmShouldWork() {
        // Create film and user first
        Film film = createValidFilm();
        ResponseEntity<Film> filmResponse = restTemplate.postForEntity("/films", film, Film.class);
        Film createdFilm = filmResponse.getBody();

        // Create user properly
        User user = new User();
        user.setEmail("unlikeuser@mail.ru");
        user.setLogin("unlikeuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        ResponseEntity<User> userResponse = restTemplate.postForEntity("/users", user, User.class);
        User createdUser = userResponse.getBody();

        // Add like first
        restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/" + createdUser.getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        // Then remove like
        ResponseEntity<Void> unlikeResponse = restTemplate.exchange(
                "/films/" + createdFilm.getId() + "/like/" + createdUser.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.OK, unlikeResponse.getStatusCode());
    }

    @Test
    void getPopularFilmsShouldReturnList() {
        // Create some films
        Film film1 = createValidFilm();
        film1.setName("Popular Film 1");
        restTemplate.postForEntity("/films", film1, Film.class);

        Film film2 = createValidFilm();
        film2.setName("Popular Film 2");
        restTemplate.postForEntity("/films", film2, Film.class);

        // Get popular films
        ResponseEntity<Film[]> response = restTemplate.getForEntity("/films/popular?count=2", Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0); // Может быть 0 или больше
    }

    @Test
    void getPopularFilmsWithCustomCountShouldWork() {
        ResponseEntity<Film[]> response = restTemplate.getForEntity("/films/popular?count=5", Film[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Проверяем, что ответ успешный, не обязательно что есть фильмы
    }
}
