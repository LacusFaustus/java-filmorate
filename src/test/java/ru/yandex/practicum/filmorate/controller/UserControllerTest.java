package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserWithValidData() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.ru");
        userDto.setLogin("testlogin");
        userDto.setBirthday(LocalDate.of(2000, 1, 1));

        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.createUser(any(User.class))).thenReturn(user);

        UserDto result = userController.createUser(userDto);
        assertNotNull(result);
        assertEquals("test@mail.ru", result.getEmail());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUserWithFutureBirthday() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.ru");
        userDto.setLogin("testlogin");
        userDto.setBirthday(LocalDate.now().plusDays(1));

        // Теперь валидация происходит в сервисе, поэтому контроллер не должен бросать исключение
        // Вместо этого сервис бросит ValidationException при вызове createUser
        when(userService.createUser(any(User.class))).thenThrow(
                new ru.yandex.practicum.filmorate.exception.ValidationException("Дата рождения не может быть в будущем")
        );

        assertThrows(ru.yandex.practicum.filmorate.exception.ValidationException.class,
                () -> userController.createUser(userDto));
        verify(userService, times(1)).createUser(any(User.class));
    }
}
