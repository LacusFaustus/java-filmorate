package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        // Гарантируем, что имя никогда не null и не пустое
        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            userName = user.getLogin();
        }
        user.setName(userName);

        user.setId(nextId.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        User existingUser = users.get(updatedUser.getId());
        if (existingUser != null) {
            updatedUser.setFriends(existingUser.getFriends());
        }

        // Гарантируем, что имя никогда не null и не пустое
        String userName = updatedUser.getName();
        if (userName == null || userName.isBlank()) {
            userName = updatedUser.getLogin();
        }
        updatedUser.setName(userName);

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

    @Override
    public void addFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user != null && friend != null) {
            user.addFriend(friendId);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user != null && friend != null) {
            user.removeFriend(friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = users.get(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getFriends().stream()
                .map(friendId -> users.get(friendId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        User user1 = users.get(userId);
        User user2 = users.get(otherId);
        if (user1 == null || user2 == null) {
            return new ArrayList<>();
        }

        Set<Integer> commonFriendIds = new HashSet<>(user1.getFriends());
        commonFriendIds.retainAll(user2.getFriends());

        return commonFriendIds.stream()
                .map(friendId -> users.get(friendId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
