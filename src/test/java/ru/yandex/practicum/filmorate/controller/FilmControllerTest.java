package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController.clearFilms();
    }

    @Test
    void shouldReturn200WhenFilmIsValid() throws Exception {
        String validJson = """
                \s{
                  "name": "Inception",
                  "description": "A thief who steals corporate secrets.",
                  "releaseDate": "2010-07-16",
                  "duration": 8880
                \s}
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Inception"));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        String json = """
                \s{
                  "name": "   ",
                  "description": "Desc",
                  "releaseDate": "2010-07-16",
                  "duration": 8880
                \s}
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Название не может быть пустым"));
    }

    @Test
    void shouldReturn400WhenDescriptionIsTooLong() throws Exception {
        String longDescription = "A".repeat(201);
        String json = """
                \s{
                  "name": "Inception",
                  "description": "%s",
                  "releaseDate": "2010-07-16",
                  "duration": 8880
                \s}
                """.formatted(longDescription);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Максимальная длина описания — 200 символов"));
    }

    @Test
    void shouldReturn400WhenReleaseDateIsBefore1895() throws Exception {
        String json = """
                \s{
                  "name": "Old Film",
                  "description": "Old",
                  "releaseDate": "1895-12-27",
                  "duration": 100
                \s}
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Дата релиза — не раньше 28 декабря 1895 года"));
    }

    @Test
    void shouldReturn400WhenDurationIsZero() throws Exception {
        String json = """
                \s{
                  "name": "Inception",
                  "description": "Desc",
                  "releaseDate": "2010-07-16",
                  "duration": 0
                \s}
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Продолжительность должна быть больше нуля"));
    }


    @Test
    void shouldReturn200WhenUpdateExistingFilm() throws Exception {
        // 1. Сначала создаем фильм
        String createJson = """
                \s{
                  "name": "Inception",
                  "description": "Desc",
                  "releaseDate": "2010-07-16",
                  "duration": 8880
                \s}
                """;
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson));

        // 2. Обновляем его
        String updateJson = """
                \s{
                  "id": 1,
                  "name": "Inception Updated",
                  "description": "New Desc",
                  "releaseDate": "2010-07-16",
                  "duration": 9000
                \s}
                """;

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Inception Updated"));
    }

    @Test
    void shouldReturn404WhenUpdateNonExistentFilm() throws Exception {
        String updateJson = """
                \s{
                  "id": 999,
                  "name": "Ghost Film",
                  "description": "Desc",
                  "releaseDate": "2010-07-16",
                  "duration": 8880
                \s}
                """;

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Фильм с ID 999 не найден"));
    }

    @Test
    void shouldReturn400WhenUpdateIdIsNull() throws Exception {
        String updateJson = """
                \s{
                  "name": "No Id Film",
                  "description": "Desc",
                  "releaseDate": "2010-07-16",
                  "duration": 8880
                \s}
                """;

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Id должен быть указан"));
    }
}