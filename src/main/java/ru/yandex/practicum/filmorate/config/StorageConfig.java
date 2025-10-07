package ru.yandex.practicum.filmorate.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

@Configuration
public class StorageConfig {

    @Bean
    @ConditionalOnProperty(name = "filmorate.storage.type", havingValue = "memory")
    public FilmStorage filmMemoryStorage() {
        return new InMemoryFilmStorage();
    }

    @Bean
    @ConditionalOnProperty(name = "filmorate.storage.type", havingValue = "memory")
    public UserStorage userMemoryStorage() {
        return new InMemoryUserStorage();
    }
}
