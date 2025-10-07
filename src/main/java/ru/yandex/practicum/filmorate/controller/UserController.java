package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос GET /users - получение всех пользователей");
        return userService.getAllUsers().stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.info("Получен запрос GET /users/{} - получение пользователя по ID", id);
        User user = userService.getUserById(id);
        return UserDto.fromUser(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users - создание пользователя: {}", userDto);
        User user = userDto.toUser();
        User createdUser = userService.createUser(user);
        return UserDto.fromUser(createdUser);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос PUT /users - обновление пользователя: {}", userDto);
        User user = userDto.toUser();
        User updatedUser = userService.updateUser(user);
        return UserDto.fromUser(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос PUT /users/{}/friends/{} - добавление в друзья", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос DELETE /users/{}/friends/{} - удаление из друзей", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable int id) {
        log.info("Получен запрос GET /users/{}/friends - получение друзей пользователя", id);
        return userService.getFriends(id).stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен запрос GET /users/{}/friends/common/{} - получение общих друзей", id, otherId);
        return userService.getCommonFriends(id, otherId).stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }
}
