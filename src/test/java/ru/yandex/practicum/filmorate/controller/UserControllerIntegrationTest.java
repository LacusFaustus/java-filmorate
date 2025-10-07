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

    private User createValidUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void debugCreateUserWithEmptyName() {
        User user = new User();
        user.setEmail("debug@mail.ru");
        user.setLogin("debuguser");
        user.setName(""); // Пустое имя
        user.setBirthday(LocalDate.of(1990, 1, 1));

        System.out.println("=== DEBUG TEST START ===");
        System.out.println("Sending user: " + user);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);
            System.out.println("Response status: " + response.getStatusCode());
            System.out.println("Response body: " + response.getBody());
            System.out.println("=== DEBUG TEST END ===");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== DEBUG TEST END WITH ERROR ===");
        }
    }

    @Test
    void createUserWithValidDataShouldReturnCreated() {
        User user = createValidUser();

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@mail.ru", response.getBody().getEmail());
        assertEquals("testuser", response.getBody().getLogin());
        assertTrue(response.getBody().getId() > 0);
    }

    @Test
    void createUserWithEmptyLoginShouldReturnBadRequest() {
        User user = createValidUser();
        user.setLogin("");

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithInvalidEmailShouldReturnBadRequest() {
        User user = createValidUser();
        user.setEmail("invalid-email");

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithFutureBirthdayShouldReturnBadRequest() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createUserWithEmptyNameShouldUseLoginAsName() {
        User user = createValidUser();
        user.setEmail("empty-name@mail.ru"); // Уникальный email
        user.setLogin("emptynamelogin");
        user.setName(""); // Пустое имя

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        // Проверяем, что имя установлено равным логину
        assertEquals("emptynamelogin", response.getBody().getName());
    }

    @Test
    void createUserWithNullNameShouldUseLoginAsName() {
        User user = createValidUser();
        user.setEmail("null-name@mail.ru"); // Уникальный email
        user.setLogin("nullnamelogin");
        user.setName(null); // Null имя

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        // Проверяем, что имя установлено равным логину
        assertEquals("nullnamelogin", response.getBody().getName());
    }

    @Test
    void createUserWithSpacesInLoginShouldReturnBadRequest() {
        User user = createValidUser();
        user.setLogin("test user"); // Пробел в логине

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUserWithValidDataShouldReturnOk() {
        // First create a user
        User user = createValidUser();
        user.setEmail("original@mail.ru");
        user.setLogin("originaluser");

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
    void updateUserWithEmptyNameShouldUseLoginAsName() {
        // First create a user
        User user = createValidUser();
        user.setEmail("update-empty@mail.ru"); // Уникальный email
        user.setLogin("updateempty");
        user.setName("Original Name");

        ResponseEntity<User> createResponse = restTemplate.postForEntity("/users", user, User.class);
        User createdUser = createResponse.getBody();

        // Then update with empty name
        createdUser.setName("");

        ResponseEntity<User> updateResponse = restTemplate.exchange(
                "/users",
                HttpMethod.PUT,
                new HttpEntity<>(createdUser),
                User.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        // Проверяем, что имя установлено равным логину
        assertEquals("updateempty", updateResponse.getBody().getName());
    }

    @Test
    void updateNonExistentUserShouldReturnNotFound() {
        User user = createValidUser();
        user.setId(9999);

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
        User user = createValidUser();
        user.setEmail("getuser@mail.ru");
        user.setLogin("getuser");

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
    void getAllUsersShouldReturnList() {
        // Create first user
        User user1 = createValidUser();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        restTemplate.postForEntity("/users", user1, User.class);

        // Create second user
        User user2 = createValidUser();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        restTemplate.postForEntity("/users", user2, User.class);

        // Get all users
        ResponseEntity<User[]> response = restTemplate.getForEntity("/users", User[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);
    }

    @Test
    void addAndRemoveFriendShouldWorkCorrectly() {
        // Create two users
        User user1 = createValidUser();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");

        User user2 = createValidUser();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");

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

        // Remove friend
        restTemplate.delete("/users/" + createdUser1.getId() + "/friends/" + createdUser2.getId());

        // Check friends after removal
        ResponseEntity<User[]> friendsAfterRemovalResponse = restTemplate.getForEntity(
                "/users/" + createdUser1.getId() + "/friends",
                User[].class
        );

        assertEquals(HttpStatus.OK, friendsAfterRemovalResponse.getStatusCode());
        assertNotNull(friendsAfterRemovalResponse.getBody());
    }

    @Test
    void getCommonFriendsShouldWork() {
        // Create three users
        User user1 = createValidUser();
        user1.setEmail("common1@mail.ru");
        user1.setLogin("common1");

        User user2 = createValidUser();
        user2.setEmail("common2@mail.ru");
        user2.setLogin("common2");

        User commonFriend = createValidUser();
        commonFriend.setEmail("commonfriend@mail.ru");
        commonFriend.setLogin("commonfriend");

        ResponseEntity<User> user1Response = restTemplate.postForEntity("/users", user1, User.class);
        ResponseEntity<User> user2Response = restTemplate.postForEntity("/users", user2, User.class);
        ResponseEntity<User> commonFriendResponse = restTemplate.postForEntity("/users", commonFriend, User.class);

        User createdUser1 = user1Response.getBody();
        User createdUser2 = user2Response.getBody();
        User createdCommonFriend = commonFriendResponse.getBody();

        // Add common friend to both users
        restTemplate.exchange("/users/" + createdUser1.getId() + "/friends/" + createdCommonFriend.getId(),
                HttpMethod.PUT, null, Void.class);
        restTemplate.exchange("/users/" + createdUser2.getId() + "/friends/" + createdCommonFriend.getId(),
                HttpMethod.PUT, null, Void.class);

        // Get common friends
        ResponseEntity<User[]> commonFriendsResponse = restTemplate.getForEntity(
                "/users/" + createdUser1.getId() + "/friends/common/" + createdUser2.getId(),
                User[].class
        );

        assertEquals(HttpStatus.OK, commonFriendsResponse.getStatusCode());
        assertNotNull(commonFriendsResponse.getBody());
        assertEquals(1, commonFriendsResponse.getBody().length);
        assertEquals(createdCommonFriend.getId(), commonFriendsResponse.getBody()[0].getId());
    }
}
