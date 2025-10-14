package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createValidUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.ru");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void getUserByIdWhenUserExists() {
        User user = createValidUser();

        when(userStorage.findById(1L)).thenReturn(user);

        User result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("test@mail.ru", result.getEmail());
    }

    @Test
    void getUserByIdWhenUserNotExists() {
        when(userStorage.findById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }
}
