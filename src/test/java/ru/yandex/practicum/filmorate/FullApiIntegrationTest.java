package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.util.DatabaseCleaner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FullApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void cleanup() {
        databaseCleaner.cleanDatabase();
    }

    @Test
    void testFullUserFlow() {
        // Создание пользователя
        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.ru");
        userDto.setLogin("testuser");
        userDto.setName("Test User");
        userDto.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity("/users", userDto, UserDto.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long userId = createResponse.getBody().getId();

        // Получение пользователя по ID
        ResponseEntity<UserDto> getResponse = restTemplate.getForEntity("/users/" + userId, UserDto.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(userId, getResponse.getBody().getId());

        // Получение всех пользователей
        ResponseEntity<UserDto[]> getAllResponse = restTemplate.getForEntity("/users", UserDto[].class);
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertTrue(getAllResponse.getBody().length > 0);
    }

    @Test
    void testFullFilmFlow() {
        // Создание фильма
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Test Film");
        filmDto.setDescription("Test Description");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(1);
        mpaDto.setName("G");
        filmDto.setMpa(mpaDto);

        ResponseEntity<FilmDto> createResponse = restTemplate.postForEntity("/films", filmDto, FilmDto.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long filmId = createResponse.getBody().getId();

        // Получение фильма по ID
        ResponseEntity<FilmDto> getResponse = restTemplate.getForEntity("/films/" + filmId, FilmDto.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(filmId, getResponse.getBody().getId());

        // Получение всех фильмов
        ResponseEntity<FilmDto[]> getAllResponse = restTemplate.getForEntity("/films", FilmDto[].class);
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertTrue(getAllResponse.getBody().length > 0);
    }

    @Test
    void testGenresEndpoints() {
        // Получение всех жанров
        ResponseEntity<Genre[]> getAllResponse = restTemplate.getForEntity("/genres", Genre[].class);
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertTrue(getAllResponse.getBody().length > 0);

        // Получение жанра по ID
        ResponseEntity<Genre> getResponse = restTemplate.getForEntity("/genres/1", Genre.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(1, getResponse.getBody().getId());
    }

    @Test
    void testMpaEndpoints() {
        // Получение всех рейтингов MPA
        ResponseEntity<Mpa[]> getAllResponse = restTemplate.getForEntity("/mpa", Mpa[].class);
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertTrue(getAllResponse.getBody().length > 0);

        // Получение рейтинга по ID
        ResponseEntity<Mpa> getResponse = restTemplate.getForEntity("/mpa/1", Mpa.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(1, getResponse.getBody().getId());
    }

    @Test
    void testFriendsAndLikesFlow() {
        // Создание двух пользователей
        UserDto user1 = new UserDto();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        UserDto user2 = new UserDto();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        ResponseEntity<UserDto> user1Response = restTemplate.postForEntity("/users", user1, UserDto.class);
        ResponseEntity<UserDto> user2Response = restTemplate.postForEntity("/users", user2, UserDto.class);

        Long user1Id = user1Response.getBody().getId();
        Long user2Id = user2Response.getBody().getId();

        // Добавление в друзья
        restTemplate.put("/users/" + user1Id + "/friends/" + user2Id, null);

        // Получение друзей
        ResponseEntity<UserDto[]> friendsResponse = restTemplate.getForEntity("/users/" + user1Id + "/friends", UserDto[].class);
        assertEquals(HttpStatus.OK, friendsResponse.getStatusCode());
        assertEquals(1, friendsResponse.getBody().length);

        // Создание фильма и лайк
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Test Film for Like");
        filmDto.setDescription("Test Description");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(1);
        mpaDto.setName("G");
        filmDto.setMpa(mpaDto);

        ResponseEntity<FilmDto> filmResponse = restTemplate.postForEntity("/films", filmDto, FilmDto.class);
        Long filmId = filmResponse.getBody().getId();

        // Добавление лайка
        restTemplate.put("/films/" + filmId + "/like/" + user1Id, null);

        // Проверка популярных фильмов
        ResponseEntity<FilmDto[]> popularResponse = restTemplate.getForEntity("/films/popular?count=10", FilmDto[].class);
        assertEquals(HttpStatus.OK, popularResponse.getStatusCode());
        assertTrue(popularResponse.getBody().length > 0);
    }
}
