package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.AbstractController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController extends ApplicationController {
    Map<Integer, Film> films = new HashMap<Integer, Film>();

    @GetMapping
    public Collection<Film> index() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        film.setId(getNextId(films));
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        Film oldFilm = films.get(film.getId());
        films.put(film.getId(), film);
        return oldFilm;
    }


}
