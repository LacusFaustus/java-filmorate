package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

@Data
public class GenreDto {
    private Integer id;
    private String name;

    public static GenreDto fromGenre(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public Genre toGenre() {
        Genre genre = new Genre();
        genre.setId(this.id);
        genre.setName(this.name);
        return genre;
    }
}
