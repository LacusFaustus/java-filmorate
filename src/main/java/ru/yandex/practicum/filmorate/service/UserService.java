package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        log.debug("Получение всех пользователей из хранилища");
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        log.debug("Создание нового пользователя: {}", user);
        validateUser(user);
        User createdUser = userStorage.createUser(user);
        log.info("Создан новый пользователь с ID: {}", createdUser.getId());
        return createdUser;
    }

    public User updateUser(User user) {
        log.debug("Обновление пользователя: {}", user);

        if (!userStorage.userExists(user.getId())) {
            log.warn("Попытка обновления несуществующего пользователя с ID: {}", user.getId());
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }

        validateUser(user);
        User updatedUser = userStorage.updateUser(user);
        log.info("Обновлен пользователь с ID: {}", updatedUser.getId());
        return updatedUser;
    }

    public User getUserById(int id) {
        log.debug("Получение пользователя по ID: {}", id);
        return userStorage.getUserById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new NotFoundException("Пользователь с id=" + id + " не найден");
                });
    }

    public void addFriend(int userId, int friendId) {
        log.debug("Добавление пользователя {} в друзья пользователю {}", friendId, userId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователь {} уже является другом пользователя {}", friendId, userId);
            return;
        }

        user.addFriend(friendId);
        friend.addFriend(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        log.debug("Удаление пользователя {} из друзей пользователя {}", friendId, userId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            log.warn("Пользователь {} не является другом пользователя {}", friendId, userId);
            return;
        }

        user.removeFriend(friendId);
        friend.removeFriend(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        log.debug("Получение списка друзей пользователя {}", userId);

        User user = getUserById(userId);
        return user.getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        log.debug("Получение общих друзей пользователей {} и {}", userId, otherId);

        User user = getUserById(userId);
        User otherUser = getUserById(otherId);

        Set<Integer> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());

        return commonFriendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        log.debug("Валидация пользователя: {}", user);

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка создания/обновления пользователя с датой рождения в будущем: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        log.debug("Валидация пользователя прошла успешно");
    }
}
