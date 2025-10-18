package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateAndGetUser() {
        User createdUser = userStorage.save(testUser);

        User retrievedUser = userStorage.findById(createdUser.getId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getEmail()).isEqualTo("test@mail.ru");
        assertThat(retrievedUser.getLogin()).isEqualTo("testuser");
        assertThat(retrievedUser.getName()).isEqualTo("Test User");
    }

    @Test
    void testUpdateUser() {
        User createdUser = userStorage.save(testUser);

        createdUser.setName("Updated User");
        createdUser.setEmail("updated@mail.ru");

        User updatedUser = userStorage.update(createdUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@mail.ru");
    }

    @Test
    void testGetAllUsers() {
        userStorage.save(testUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@mail.ru");
        anotherUser.setLogin("anotheruser");
        anotherUser.setName("Another User");
        anotherUser.setBirthday(LocalDate.of(1995, 1, 1));
        userStorage.save(anotherUser);

        List<User> users = userStorage.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getLogin)
                .containsExactlyInAnyOrder("testuser", "anotheruser");
    }

    @Test
    void testUserExists() {
        User createdUser = userStorage.save(testUser);

        boolean exists = userStorage.userExists(createdUser.getId());

        assertThat(exists).isTrue();
        assertThat(userStorage.userExists(9999L)).isFalse();
    }

    @Test
    void testAddAndGetFriends() {
        User user1 = userStorage.save(testUser);

        User user2 = new User();
        user2.setEmail("friend@mail.ru");
        user2.setLogin("friend");
        user2.setName("Friend User");
        user2.setBirthday(LocalDate.of(1992, 1, 1));
        User createdUser2 = userStorage.save(user2);

        userStorage.addFriend(user1.getId(), createdUser2.getId());

        List<User> friends = userStorage.getFriends(user1.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(createdUser2.getId());
    }
}
