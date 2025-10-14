package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<FilmDto> getAllFilms() {
        return filmService.getAllFilms().stream()
                .map(FilmDto::fromFilm)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Long id) {
        Film film = filmService.getFilmById(id);
        return FilmDto.fromFilm(film);
    }

    @PostMapping
    public FilmDto createFilm(@RequestBody FilmDto filmDto) {
        Film film = filmDto.toFilm();
        Film createdFilm = filmService.createFilm(film);
        return FilmDto.fromFilm(createdFilm);
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody FilmDto filmDto) {
        Film film = filmDto.toFilm();
        Film updatedFilm = filmService.updateFilm(film);
        return FilmDto.fromFilm(updatedFilm);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count,
                                      @RequestParam(required = false) Integer page) {
        if (page != null) {
            return filmService.getPopularFilms(count, page);
        }
        return filmService.getPopularFilms(count);
    }
}
