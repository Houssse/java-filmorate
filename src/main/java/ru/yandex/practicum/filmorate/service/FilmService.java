package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validations.FilmValidator;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public  FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getById(id);
        if (Objects.isNull(film)) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
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

    public void addLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }

        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким ID " + userId + " не найден");
        }

        film.getLikes().add(userId);
    }

    public void deleteLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }

        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким ID " + userId + " не найден");
        }

        film.getLikes().remove(userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}