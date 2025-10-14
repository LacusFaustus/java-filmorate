package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.util.DatabaseCleaner;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private AtomicLong userCounter = new AtomicLong(System.currentTimeMillis());

    @BeforeEach
    void cleanup() {
        databaseCleaner.cleanDatabase();
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
    void createUserWithValidDataShouldReturnCreated() {
        UserDto userDto = createValidUserDto();

        ResponseEntity<UserDto> response = restTemplate.postForEntity("/users", userDto, UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userDto.getEmail(), response.getBody().getEmail());
        assertTrue(response.getBody().getId() > 0);
    }

    @Test
    void updateNonExistentUserShouldReturnNotFound() {
        UserDto userDto = createValidUserDto();
        userDto.setId(9999L);

        ResponseEntity<String> response = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(userDto),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
