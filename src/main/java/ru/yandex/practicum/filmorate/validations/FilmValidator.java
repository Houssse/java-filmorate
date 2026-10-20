package ru.yandex.practicum.filmorate.validations;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    private static final int MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    public static boolean checkFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата показа не может быть пустой");
        }
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null) {
            throw new ValidationException("Продолжительность фильма не может быть пустой");
        }
        if (!film.getDuration().isPositive()) {
            throw new ValidationException("Продолжительность должна быть больше нуля");
        }

        return true;
    }
}