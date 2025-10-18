package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FilmDto {
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaDto mpa;
    private List<GenreDto> genres = new ArrayList<>();
    private List<Long> likes = new ArrayList<>();

    public static FilmDto fromFilm(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            dto.setMpa(MpaDto.fromMpa(film.getMpa()));
        }

        if (film.getGenres() != null) {
            dto.setGenres(film.getGenres().stream()
                    .map(GenreDto::fromGenre)
                    .collect(Collectors.toList()));
        }

        if (film.getLikes() != null) {
            dto.setLikes(new ArrayList<>(film.getLikes()));
        }

        return dto;
    }

    public Film toFilm() {
        Film film = new Film();
        film.setId(this.id);
        film.setName(this.name);

        if (this.description != null) {
            film.setDescription(this.description.length() > MAX_DESCRIPTION_LENGTH
                    ? this.description.substring(0, MAX_DESCRIPTION_LENGTH)
                    : this.description);
        } else {
            film.setDescription(null);
        }

        film.setReleaseDate(this.releaseDate);
        film.setDuration(this.duration);

        if (this.mpa != null) {
            film.setMpa(this.mpa.toMpa());
        }

        if (this.genres != null) {
            film.setGenres(this.genres.stream()
                    .map(GenreDto::toGenre)
                    .collect(Collectors.toList()));
        }

        if (this.likes != null) {
            film.setLikes(new HashSet<>(this.likes));
        }

        return film;
    }
}
