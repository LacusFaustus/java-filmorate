package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    // Добавляем метод для пагинации
    public List<User> getAllUsers(int page, int size) {
        List<User> allUsers = userStorage.findAll();
        int fromIndex = (page - 1) * size;
        if (fromIndex >= allUsers.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, allUsers.size());
        return allUsers.subList(fromIndex, toIndex);
    }

    public User getUserById(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user;
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.save(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        User updatedUser = userStorage.update(user);
        if (updatedUser == null) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        return updatedUser;
    }

    public void deleteUser(Long id) {
        userStorage.delete(id);
    }

    public void addFriend(Long userId, Long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        validateUserExists(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        validateUserExists(userId);
        validateUserExists(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    private void validateUser(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUserExists(Long userId) {
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }
}
