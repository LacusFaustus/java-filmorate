package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void getAllUsersShouldReturnEmptyListInitially() {
        List<User> users = userStorage.findAll();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void saveUserShouldAssignIdAndAddToStorage() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.save(user);

        assertNotNull(createdUser);
        assertTrue(createdUser.getId() > 0);
        assertEquals("test@mail.ru", createdUser.getEmail());
        assertEquals("testuser", createdUser.getLogin());

        List<User> users = userStorage.findAll();
        assertEquals(1, users.size());
        assertEquals(createdUser.getId(), users.get(0).getId());
    }

    @Test
    void saveUserWithEmptyNameShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.save(user);

        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void saveUserWithNullNameShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.save(user);

        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void updateUserShouldReplaceExistingUser() {
        // Create user first
        User user = new User();
        user.setEmail("original@mail.ru");
        user.setLogin("originaluser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.save(user);

        // Update user
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@mail.ru");
        updatedUser.setLogin("updateduser");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));

        User result = userStorage.update(updatedUser);

        assertEquals("updated@mail.ru", result.getEmail());
        assertEquals("updateduser", result.getLogin());

        User retrievedUser = userStorage.findById(createdUser.getId());
        assertNotNull(retrievedUser);
        assertEquals("updated@mail.ru", retrievedUser.getEmail());
    }

    @Test
    void updateUserWithEmptyNameShouldUseLoginAsName() {
        // Create user first
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName("Original Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.save(user);

        // Update user with empty name
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("test@mail.ru");
        updatedUser.setLogin("testuser");
        updatedUser.setName("");
        updatedUser.setBirthday(LocalDate.of(1990, 1, 1));

        User result = userStorage.update(updatedUser);

        assertEquals("testuser", result.getName());
    }

    @Test
    void findUserByIdShouldReturnUserWhenExists() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.save(user);

        User retrievedUser = userStorage.findById(createdUser.getId());

        assertNotNull(retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals("test@mail.ru", retrievedUser.getEmail());
    }

    @Test
    void findUserByIdShouldReturnNullWhenNotExists() {
        User user = userStorage.findById(9999L);
        assertNull(user);
    }

    @Test
    void deleteUserShouldRemoveUserFromStorage() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.save(user);

        // Verify user exists
        assertTrue(userStorage.userExists(createdUser.getId()));

        // Delete user
        userStorage.delete(createdUser.getId());

        // Verify user no longer exists
        assertFalse(userStorage.userExists(createdUser.getId()));
        User deletedUser = userStorage.findById(createdUser.getId());
        assertNull(deletedUser);
    }

    @Test
    void userExistsShouldReturnTrueForExistingUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.save(user);

        assertTrue(userStorage.userExists(createdUser.getId()));
    }

    @Test
    void userExistsShouldReturnFalseForNonExistingUser() {
        assertFalse(userStorage.userExists(9999L));
    }

    @Test
    void multipleUsersShouldBeStoredCorrectly() {
        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        User createdUser1 = userStorage.save(user1);
        User createdUser2 = userStorage.save(user2);

        List<User> users = userStorage.findAll();
        assertEquals(2, users.size());

        assertTrue(users.stream().anyMatch(u -> u.getId().equals(createdUser1.getId())));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(createdUser2.getId())));
    }

    @Test
    void userFriendsShouldBePreservedAfterUpdate() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.addFriend(123L);

        User createdUser = userStorage.save(user);

        // Verify friend exists
        assertTrue(createdUser.getFriends().contains(123L));

        // Update user
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@mail.ru");
        updatedUser.setLogin("updateduser");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));
        // Note: we're not setting friends here, they should be preserved from the original

        User result = userStorage.update(updatedUser);

        // Friends should be preserved
        assertTrue(result.getFriends().contains(123L));
    }
}
