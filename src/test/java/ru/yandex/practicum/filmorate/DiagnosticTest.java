package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.UserDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiagnosticTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void debugFilmCreation() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Test Film");
        filmDto.setDescription("Test Description");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(1);
        mpaDto.setName("G");
        filmDto.setMpa(mpaDto);

        System.out.println("=== FILM DTO ===");
        System.out.println("Name: " + filmDto.getName());
        System.out.println("MPA ID: " + filmDto.getMpa().getId());

        ResponseEntity<String> response = restTemplate.postForEntity("/films", filmDto, String.class);
        System.out.println("=== RESPONSE ===");
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void debugUserCreation() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.ru");
        userDto.setLogin("testuser");
        userDto.setName("Test User");
        userDto.setBirthday(LocalDate.of(1990, 1, 1));

        System.out.println("=== USER DTO ===");
        System.out.println("Email: " + userDto.getEmail());
        System.out.println("Login: " + userDto.getLogin());

        ResponseEntity<String> response = restTemplate.postForEntity("/users", userDto, String.class);
        System.out.println("=== RESPONSE ===");
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
