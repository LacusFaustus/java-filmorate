package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        log.info("Creating user: {}", user);
        try {
            validateUser(user);
            // Обрабатываем имя перед сохранением
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            log.info("Processed user name: {}", user.getName());
            User createdUser = userStorage.createUser(user);
            log.info("Successfully created user with id: {}", createdUser.getId());
            return createdUser;
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    public User updateUser(User user) {
        log.info("Updating user: {}", user);
        try {
            if (!userStorage.userExists(user.getId())) {
                throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
            }
            validateUser(user);
            // Обрабатываем имя перед обновлением
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            log.info("Processed user name for update: {}", user.getName());
            User updatedUser = userStorage.updateUser(user);
            log.info("Successfully updated user with id: {}", updatedUser.getId());
            return updatedUser;
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    private void validateUser(User user) {
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
