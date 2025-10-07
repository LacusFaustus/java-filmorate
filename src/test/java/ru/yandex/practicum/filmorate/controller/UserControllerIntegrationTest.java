package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createUserWithValidDataShouldReturnCreated() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@mail.ru", response.getBody().getEmail());
        assertEquals("testuser", response.getBody().getLogin());
        assertTrue(response.getBody().getId() > 0);
    }

    @Test
    void createUserWithEmptyLoginShouldReturnBadRequest() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithInvalidEmailShouldReturnBadRequest() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithFutureBirthdayShouldReturnBadRequest() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().plusDays(1));

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithEmptyNameShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getName());
    }

    @Test
    void updateUserWithValidDataShouldReturnOk() {
        // First create a user
        User user = new User();
        user.setEmail("original@mail.ru");
        user.setLogin("originaluser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<User> createResponse = restTemplate.postForEntity("/users", user, User.class);
        User createdUser = createResponse.getBody();

        // Then update it
        createdUser.setEmail("updated@mail.ru");
        createdUser.setLogin("updateduser");

        ResponseEntity<User> updateResponse = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(createdUser),
                User.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("updated@mail.ru", updateResponse.getBody().getEmail());
    }

    @Test
    void updateNonExistentUserShouldReturnNotFound() {
        User user = new User();
        user.setId(9999);
        user.setEmail("nonexistent@mail.ru");
        user.setLogin("nonexistent");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(user),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserByIdShouldReturnUser() {
        // First create a user
        User user = new User();
        user.setEmail("getuser@mail.ru");
        user.setLogin("getuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<User> createResponse = restTemplate.postForEntity("/users", user, User.class);
        User createdUser = createResponse.getBody();

        // Then get it by ID
        ResponseEntity<User> getResponse = restTemplate.getForEntity(
                "/users/" + createdUser.getId(),
                User.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("getuser@mail.ru", getResponse.getBody().getEmail());
    }

    @Test
    void getNonExistentUserShouldReturnNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/users/9999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void addAndRemoveFriendShouldWorkCorrectly() {
        // Create two users
        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        ResponseEntity<User> user1Response = restTemplate.postForEntity("/users", user1, User.class);
        ResponseEntity<User> user2Response = restTemplate.postForEntity("/users", user2, User.class);

        User createdUser1 = user1Response.getBody();
        User createdUser2 = user2Response.getBody();

        // Add friend
        restTemplate.put("/users/" + createdUser1.getId() + "/friends/" + createdUser2.getId(), null);

        // Check friends
        ResponseEntity<User[]> friendsResponse = restTemplate.getForEntity(
                "/users/" + createdUser1.getId() + "/friends",
                User[].class
        );

        assertEquals(HttpStatus.OK, friendsResponse.getStatusCode());
        assertNotNull(friendsResponse.getBody());
        assertEquals(1, friendsResponse.getBody().length);

        // Remove friend
        restTemplate.delete("/users/" + createdUser1.getId() + "/friends/" + createdUser2.getId());

        // Check friends after removal
        ResponseEntity<User[]> friendsAfterRemovalResponse = restTemplate.getForEntity(
                "/users/" + createdUser1.getId() + "/friends",
                User[].class
        );

        assertEquals(HttpStatus.OK, friendsAfterRemovalResponse.getStatusCode());
        assertNotNull(friendsAfterRemovalResponse.getBody());
        assertEquals(0, friendsAfterRemovalResponse.getBody().length);
    }
}
