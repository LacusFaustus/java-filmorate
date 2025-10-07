package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(nextId.getAndIncrement());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        // Получаем существующего пользователя для сохранения его друзей
        User existingUser = users.get(updatedUser.getId());
        if (existingUser != null) {
            // Сохраняем друзей из существующего пользователя
            updatedUser.setFriends(existingUser.getFriends());
        }

        if (updatedUser.getName() == null || updatedUser.getName().isBlank()) {
            updatedUser.setName(updatedUser.getLogin());
        }
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }

    @Override
    public boolean userExists(int id) {
        return users.containsKey(id);
    }
}
