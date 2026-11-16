package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.Collection;


@RestController
@RequestMapping("/films")
public class FilmController {
    @Autowired
    private FilmService filmService;

    @GetMapping
    public Collection<Film> index() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return  filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return  filmService.update(newFilm);
    }

    @PutMapping("{userId}/like/{filmId}")
    public void like(@PathVariable long userId, @PathVariable long filmId) {
        filmService.addLike(userId, filmId);
    }

    @DeleteMapping("{userId/like/{filmId")
    public void unlike(@PathVariable long userId, @PathVariable long filmId) {
        filmService.deleteLike(userId, filmId);
    }

    @GetMapping("/top")
    public Collection<Film> top10() {
        return filmService.getTopFilms();
    }
}
