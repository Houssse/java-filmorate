package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @InjectMocks
    private FilmService filmService;

    // ==================== CREATE ====================

    @Test
    void shouldCreateFilmSuccessfully() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("Good film");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(120);

        when(filmStorage.getNextId()).thenReturn(1L);
        when(filmStorage.create(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Film created = filmService.create(film);

        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Inception", created.getName());
        assertEquals(120, created.getDuration());
        verify(filmStorage, times(1)).create(film);
    }

    // ==================== UPDATE ====================

    @Test
    void shouldUpdateFilmSuccessfully() {
        Film oldFilm = new Film();
        oldFilm.setId(1L);
        oldFilm.setName("Old Name");
        oldFilm.setDescription("Old Desc");
        oldFilm.setDuration(100);

        Film newFilm = new Film();
        newFilm.setId(1L);
        newFilm.setName("New Name");

        when(filmStorage.getById(1L)).thenReturn(oldFilm);
        when(filmStorage.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Film updated = filmService.update(newFilm);

        assertEquals("New Name", updated.getName());
        assertEquals("Old Desc", updated.getDescription());
        assertEquals(100, updated.getDuration());
        verify(filmStorage, times(1)).update(oldFilm);
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentFilm() {
        Film newFilm = new Film();
        newFilm.setId(999L);

        when(filmStorage.getById(999L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> filmService.update(newFilm));
        verify(filmStorage, never()).update(any());
    }
}