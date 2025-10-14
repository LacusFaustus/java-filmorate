package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();
    User findById(Long id);
    User save(User user);
    User update(User user);
    void delete(Long id);
    void addFriend(Long userId, Long friendId);
    void removeFriend(Long userId, Long friendId);
    List<User> getFriends(Long userId);
    List<User> getCommonFriends(Long userId, Long otherUserId);

    // Добавляем методы для тестов
    default Optional<User> getUserById(Long id) {
        User user = findById(id);
        return user != null ? Optional.of(user) : Optional.empty();
    }

    default boolean userExists(Long id) {
        return findById(id) != null;
    }
}
