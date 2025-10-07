package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        loadFriendsForUsers(users);
        return users;
    }

    @Override
    public User createUser(User user) {
        // Имя уже обработано в сервисе
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        parameters.put("name", user.getName());
        parameters.put("birthday", user.getBirthday());

        Number key = simpleJdbcInsert.executeAndReturnKey(parameters);
        user.setId(key.intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        // Имя уже обработано в сервисе
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (updated == 0) {
            return user;
        }

        return getUserById(user.getId()).orElse(user);
    }

    @Override
    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        User user = users.get(0);
        loadFriendsForUser(user);
        return Optional.of(user);
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean userExists(int id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        // Не добавляем обратную связь для односторонней дружбы
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT u.* FROM users u JOIN friendships f ON u.id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendships f1 ON u.id = f1.friend_id " +
                "JOIN friendships f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId, otherId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("test" + System.currentTimeMillis() + "@mail.ru");
        user.setLogin("testuser" + System.currentTimeMillis());
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private void loadFriendsForUser(User user) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        List<Integer> friendIds = jdbcTemplate.queryForList(sql, Integer.class, user.getId());
        user.setFriends(new HashSet<>(friendIds));
    }

    private void loadFriendsForUsers(List<User> users) {
        for (User user : users) {
            loadFriendsForUser(user);
        }
    }
}
