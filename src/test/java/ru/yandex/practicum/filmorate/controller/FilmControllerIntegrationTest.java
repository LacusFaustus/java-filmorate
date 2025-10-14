package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.dto.MpaDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.util.DatabaseCleaner;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private AtomicLong filmCounter = new AtomicLong(System.currentTimeMillis());
    private AtomicLong userCounter = new AtomicLong(System.currentTimeMillis() + 10000);

    @BeforeEach
    void cleanup() {
        databaseCleaner.cleanDatabase();
    }

    private FilmDto createValidFilmDto() {
        long counter = filmCounter.incrementAndGet();
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Test Film " + counter);
        filmDto.setDescription("Test Description " + counter);
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(1);
        mpaDto.setName("G");
        filmDto.setMpa(mpaDto);

        return filmDto;
    }

    private UserDto createValidUserDto() {
        long counter = userCounter.incrementAndGet();
        UserDto userDto = new UserDto();
        userDto.setEmail("test" + counter + "@mail.ru");
        userDto.setLogin("testuser" + counter);
        userDto.setName("Test User " + counter);
        userDto.setBirthday(LocalDate.of(1990, 1, 1));
        return userDto;
    }

    @Test
    void createFilmWithValidDataShouldReturnCreated() {
        FilmDto filmDto = createValidFilmDto();

        ResponseEntity<FilmDto> response = restTemplate.postForEntity("/films", filmDto, FilmDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(filmDto.getName(), response.getBody().getName());
        assertTrue(response.getBody().getId() > 0);
    }

    @Test
    void createUserWithValidDataShouldReturnCreated() {
        UserDto userDto = createValidUserDto();

        ResponseEntity<UserDto> response = restTemplate.postForEntity("/users", userDto, UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userDto.getEmail(), response.getBody().getEmail());
        assertTrue(response.getBody().getId() > 0);
    }
}
