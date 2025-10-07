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
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void getUserByIdWhenUserExists() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@mail.ru");

        when(userStorage.getUserById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);
        assertNotNull(result);
        assertEquals("test@mail.ru", result.getEmail());
    }

    @Test
    void getUserByIdWhenUserNotExists() {
        when(userStorage.getUserById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1));
    }
}
