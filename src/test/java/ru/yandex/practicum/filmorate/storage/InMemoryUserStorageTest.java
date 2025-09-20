package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void getAllUsersShouldReturnEmptyListInitially() {
        List<User> users = userStorage.getAllUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void createUserShouldAssignIdAndAddToStorage() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(user);

        assertNotNull(createdUser);
        assertTrue(createdUser.getId() > 0);
        assertEquals("test@mail.ru", createdUser.getEmail());
        assertEquals("testuser", createdUser.getLogin());

        List<User> users = userStorage.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(createdUser.getId(), users.get(0).getId());
    }

    @Test
    void createUserWithEmptyNameShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(user);

        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void createUserWithNullNameShouldUseLoginAsName() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(user);

        assertEquals("testuser", createdUser.getName());
    }

    @Test
    void updateUserShouldReplaceExistingUser() {
        // Create user first
        User user = new User();
        user.setEmail("original@mail.ru");
        user.setLogin("originaluser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(user);

        // Update user
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@mail.ru");
        updatedUser.setLogin("updateduser");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));

        User result = userStorage.updateUser(updatedUser);

        assertEquals("updated@mail.ru", result.getEmail());
        assertEquals("updateduser", result.getLogin());

        Optional<User> retrievedUser = userStorage.getUserById(createdUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals("updated@mail.ru", retrievedUser.get().getEmail());
    }

    @Test
    void updateUserWithEmptyNameShouldUseLoginAsName() {
        // Create user first
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName("Original Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(user);

        // Update user with empty name
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("test@mail.ru");
        updatedUser.setLogin("testuser");
        updatedUser.setName("");
        updatedUser.setBirthday(LocalDate.of(1990, 1, 1));

        User result = userStorage.updateUser(updatedUser);

        assertEquals("testuser", result.getName());
    }

    @Test
    void getUserByIdShouldReturnUserWhenExists() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(user);

        Optional<User> retrievedUser = userStorage.getUserById(createdUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals(createdUser.getId(), retrievedUser.get().getId());
        assertEquals("test@mail.ru", retrievedUser.get().getEmail());
    }

    @Test
    void getUserByIdShouldReturnEmptyWhenNotExists() {
        Optional<User> user = userStorage.getUserById(9999);
        assertFalse(user.isPresent());
    }

    @Test
    void deleteUserShouldRemoveUserFromStorage() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(user);

        // Verify user exists
        assertTrue(userStorage.userExists(createdUser.getId()));

        // Delete user
        userStorage.deleteUser(createdUser.getId());

        // Verify user no longer exists
        assertFalse(userStorage.userExists(createdUser.getId()));
        Optional<User> deletedUser = userStorage.getUserById(createdUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void userExistsShouldReturnTrueForExistingUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(user);

        assertTrue(userStorage.userExists(createdUser.getId()));
    }

    @Test
    void userExistsShouldReturnFalseForNonExistingUser() {
        assertFalse(userStorage.userExists(9999));
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

        User createdUser1 = userStorage.createUser(user1);
        User createdUser2 = userStorage.createUser(user2);

        List<User> users = userStorage.getAllUsers();
        assertEquals(2, users.size());

        assertTrue(users.stream().anyMatch(u -> u.getId() == createdUser1.getId()));
        assertTrue(users.stream().anyMatch(u -> u.getId() == createdUser2.getId()));
    }

    @Test
    void userFriendsShouldBePreservedAfterUpdate() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.addFriend(123);

        User createdUser = userStorage.createUser(user);

        // Verify friend exists
        assertTrue(createdUser.getFriends().contains(123));

        // Update user
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@mail.ru");
        updatedUser.setLogin("updateduser");
        updatedUser.setBirthday(LocalDate.of(1991, 1, 1));
        // Note: we're not setting friends here, they should be preserved from the original

        User result = userStorage.updateUser(updatedUser);

        // Friends should be preserved
        assertTrue(result.getFriends().contains(123));
    }
}
