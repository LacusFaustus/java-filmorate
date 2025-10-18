package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();
    private Set<Long> likes = new HashSet<>();

    // Добавляем методы для работы с рейтингом
    public Long getRate() {
        return (long) likes.size();
    }

    public void setRate(Long rate) {
        // Rate вычисляется автоматически на основе лайков
        // Этот метод может быть пустым или использоваться для синхронизации
    }

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
    }

    public int getLikesCount() {
        return likes.size();
    }
}
