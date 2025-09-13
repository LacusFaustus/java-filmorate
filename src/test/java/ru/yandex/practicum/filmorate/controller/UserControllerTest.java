package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUserWithValidData() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ResponseEntity<User> response = userController.createUser(user);
        assertNotNull(response.getBody());
        assertEquals("test@mail.ru", response.getBody().getEmail());
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void createUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin(""); // Пустой логин - должно пройти, так как аннотации не работают
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // В unit-тестах аннотации @NotBlank не работают
        ResponseEntity<User> response = userController.createUser(user);
        assertNotNull(response.getBody());
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void createUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.now().plusDays(1)); // Дата в будущем

        // Эта проверка должна выбрасывать ValidationException из нашей кастомной валидации
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void createUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email"); // Неправильный email
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // В unit-тестах аннотации @Email не работают
        ResponseEntity<User> response = userController.createUser(user);
        assertNotNull(response.getBody());
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void createUserWithEmptyNameShouldUseLogin() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setName(""); // Пустое имя
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ResponseEntity<User> response = userController.createUser(user);
        assertNotNull(response.getBody());
        assertEquals("testlogin", response.getBody().getName()); // Должно использовать логин
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void createUserWithNullNameShouldUseLogin() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setName(null); // null имя
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ResponseEntity<User> response = userController.createUser(user);
        assertNotNull(response.getBody());
        assertEquals("testlogin", response.getBody().getName()); // Должно использовать логин
        assertEquals(201, response.getStatusCodeValue());
    }
}
