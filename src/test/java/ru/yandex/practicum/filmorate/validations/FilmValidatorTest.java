package ru.yandex.practicum.filmorate.validations;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {
    @Test
    void shouldThrowWhenNameIsNull() {
        Film film = new Film();
        film.setName(null);
        assertThrows(ValidationException.class, () -> FilmValidator.checkFilmName(film));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        Film film = new Film();
        film.setName("   ");
        assertThrows(ValidationException.class, () -> FilmValidator.checkFilmName(film));
    }

    @Test
    void shouldNotThrowWhenNameIsValid() {
        Film film = new Film();
        film.setName("Inception");
        assertDoesNotThrow(() -> FilmValidator.checkFilmName(film));
    }

    @Test
    void shouldNotThrowWhenDescriptionIsExactly200Chars() {
        Film film = new Film();
        film.setDescription("A".repeat(200));
        assertDoesNotThrow(() -> FilmValidator.checkFilmDescription(film));
    }

    @Test
    void shouldThrowWhenDescriptionIs201Chars() {
        Film film = new Film();
        film.setDescription("A".repeat(201));
        assertThrows(ValidationException.class, () -> FilmValidator.checkFilmDescription(film));
    }

    @Test
    void shouldNotThrowWhenDateIsExactlyMinDate() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> FilmValidator.checkFilmReleaseDate(film));
    }

    @Test
    void shouldThrowWhenDateIsBeforeMinDate() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> FilmValidator.checkFilmReleaseDate(film));
    }

    @Test
    void shouldThrowWhenDurationIsZero() {
        Film film = new Film();
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> FilmValidator.checkFilmDuration(film));
    }

    @Test
    void shouldThrowWhenDurationIsNegative() {
        Film film = new Film();
        film.setDuration(-10);
        assertThrows(ValidationException.class, () -> FilmValidator.checkFilmDuration(film));
    }

    @Test
    void shouldNotThrowWhenDurationIsPositive() {
        Film film = new Film();
        film.setDuration(1); // минимально допустимая
        assertDoesNotThrow(() -> FilmValidator.checkFilmDuration(film));
    }
}