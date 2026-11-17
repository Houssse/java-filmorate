package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FilmService filmService;

    private Film sampleFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Интерстеллар");
        film.setDescription("Космос");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);
        return film;
    }

    @Test
    void index_returnsAllFilms() throws Exception {
        when(filmService.getAllFilms()).thenReturn(List.of(sampleFilm()));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Интерстеллар"));

        verify(filmService).getAllFilms();
    }

    @Test
    void create_returnsCreatedFilm() throws Exception {
        when(filmService.create(any(Film.class))).thenReturn(sampleFilm());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleFilm())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Интерстеллар"));

        verify(filmService).create(any(Film.class));
    }

    @Test
    void create_withInvalidFilm_returnsBadRequest() throws Exception {
        when(filmService.create(any(Film.class)))
                .thenThrow(new ValidationException("Название не может быть пустым"));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"duration\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Название не может быть пустым"));
    }

    @Test
    void update_returnsUpdatedFilm() throws Exception {
        when(filmService.update(any(Film.class))).thenReturn(sampleFilm());

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleFilm())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_whenNotFound_returnsNotFound() throws Exception {
        when(filmService.update(any(Film.class)))
                .thenThrow(new NotFoundException("Фильм с ID 99 не найден"));

        Film f = sampleFilm();
        f.setId(99L);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(f)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Фильм с ID 99 не найден"));
    }

    @Test
    void like_callsService() throws Exception {
        doNothing().when(filmService).addLike(1L, 2L);

        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());

        verify(filmService).addLike(1L, 2L);
    }

    @Test
    void like_whenUserNotFound_returnsNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь с таким ID 2 не найден"))
                .when(filmService).addLike(1L, 2L);

        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с таким ID 2 не найден"));
    }

    @Test
    void unlike_callsService() throws Exception {
        doNothing().when(filmService).deleteLike(1L, 2L);

        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());

        verify(filmService).deleteLike(1L, 2L);
    }

    @Test
    void getPopularFilms_defaultCount() throws Exception {
        when(filmService.getPopularFilms(10)).thenReturn(List.of(sampleFilm()));

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(filmService).getPopularFilms(10);
    }

    @Test
    void getPopularFilms_customCount() throws Exception {
        when(filmService.getPopularFilms(2)).thenReturn(List.of());

        mockMvc.perform(get("/films/popular").param("count", "2"))
                .andExpect(status().isOk());

        verify(filmService).getPopularFilms(2);
    }
}