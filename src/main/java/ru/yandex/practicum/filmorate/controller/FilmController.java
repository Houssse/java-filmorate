package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validations.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends ApplicationController {
    Map<Long, Film> films = new HashMap<Long, Film>();

    @GetMapping
    public Collection<Film> index() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("Создание фильма {}", film);

        if (FilmValidator.checkFilm(film)) {
            film.setId(getNextId(films));
            films.put(film.getId(), film);
        }

        log.debug("Фильма создан {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.debug("Обновление фильма {}", newFilm);

        FilmValidator.checkFilmId(newFilm);

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            throw  new NotFoundException("Фильм с ID " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null) {
            FilmValidator.checkFilmName(newFilm);
            films.put(newFilm.getId(), newFilm);
        }

        if (newFilm.getDescription() != null) {
            FilmValidator.checkFilmDescription(newFilm);
            films.put(newFilm.getId(), newFilm);
        }

        if (newFilm.getReleaseDate() != null) {
            FilmValidator.checkFilmReleaseDate(newFilm);
            films.put(newFilm.getId(), newFilm);
        }

        if (newFilm.getDuration() != null) {
            FilmValidator.checkFilmDuration(newFilm);
            films.put(newFilm.getId(), newFilm);
        }

        log.debug("Фильм обновлен {}", oldFilm);
        return oldFilm;
    }
}
