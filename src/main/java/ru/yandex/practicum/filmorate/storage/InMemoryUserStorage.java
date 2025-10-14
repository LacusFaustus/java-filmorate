package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private long currentId = 1L;

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>(users.values());
        // Загружаем друзей для каждого пользователя
        userList.forEach(this::loadFriends);
        return userList;
    }

    @Override
    public User findById(Long id) {
        User user = users.get(id);
        if (user != null) {
            loadFriends(user);
        }
        return user;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(currentId);
            currentId++;
        }

        // Автоматически устанавливаем login как name, если name пустое или null
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        // Сохраняем пользователя
        users.put(user.getId(), user);

        // Инициализируем список друзей
        friends.put(user.getId(), new HashSet<>());

        // Если у пользователя уже есть друзья, сохраняем их
        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            friends.get(user.getId()).addAll(user.getFriends());
        }

        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            // Сохраняем существующих друзей
            Set<Long> existingFriends = friends.get(user.getId());

            // Автоматически устанавливаем login как name, если name пустое или null
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }

            // Обновляем пользователя
            users.put(user.getId(), user);

            // Восстанавливаем друзей
            friends.put(user.getId(), existingFriends);

            // Загружаем друзей в объект пользователя для возврата
            loadFriends(user);
            return user;
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
        friends.remove(id);
        // Удаляем из списков друзей других пользователей
        friends.values().forEach(friendSet -> friendSet.remove(id));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (friends.containsKey(userId) && users.containsKey(friendId)) {
            friends.get(userId).add(friendId);
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        if (!friends.containsKey(userId)) {
            return List.of();
        }
        return friends.get(userId).stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        Set<Long> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Long> otherUserFriends = friends.getOrDefault(otherUserId, Collections.emptySet());

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Вспомогательный метод для загрузки друзей в объект пользователя
    private void loadFriends(User user) {
        Set<Long> userFriends = friends.get(user.getId());
        if (userFriends != null) {
            user.setFriends(new HashSet<>(userFriends));
        } else {
            user.setFriends(new HashSet<>());
        }
    }

    // Методы для обратной совместимости с тестами
    public List<User> getAllUsers() {
        return findAll();
    }

    public User createUser(User user) {
        return save(user);
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(findById(id));
    }

    public boolean userExists(Long id) {
        return users.containsKey(id);
    }

    public void deleteUser(Long id) {
        delete(id);
    }

    public User updateUser(User user) {
        return update(user);
    }
}
