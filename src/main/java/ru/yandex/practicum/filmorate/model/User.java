package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;

    @Email(message = "Электронная почта должна быть корректного формата")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;

    private String name; // Без валидации - может быть null или пустым
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();

    public User() {
        this.friends = new HashSet<>();
    }

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        friends.remove(friendId);
    }

    public Set<Integer> getFriends() {
        return new HashSet<>(friends);
    }

    public void setFriends(Set<Integer> friends) {
        this.friends = new HashSet<>(friends);
    }
}
