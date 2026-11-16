package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validations.FilmValidator;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    FilmStorage filmStorage;
    @Autowired
    public void setFilmStorage(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film create(Film film) {
        log.debug("Создание фильма {}", film);

        if (FilmValidator.checkFilm(film)) {
            film.setId(filmStorage.getNextId());
        }

        log.debug("Фильма создан {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.debug("Обновление фильма {}", newFilm);

        FilmValidator.checkFilmId(newFilm);

        Film oldFilm = filmStorage.getById(newFilm.getId());
        if (oldFilm == null) {
            throw new NotFoundException("Фильм с ID " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null) {
            FilmValidator.checkFilmName(newFilm);
            oldFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            FilmValidator.checkFilmDescription(newFilm);
            oldFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null) {
            FilmValidator.checkFilmReleaseDate(newFilm);
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null) {
            FilmValidator.checkFilmDuration(newFilm);
            oldFilm.setDuration(newFilm.getDuration());
        }
        log.debug("Фильм обновлен {}", oldFilm);
        return filmStorage.update(oldFilm);
    }
}
