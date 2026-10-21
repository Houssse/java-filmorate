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

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController.clearUsers();
    }


    @Test
    void shouldReturn200WhenUserIsValid() throws Exception {
        String validJson = """
                {
                  "email": "test@mail.ru",
                  "login": "testLogin",
                  "name": "Test Name",
                  "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testLogin"))
                .andExpect(jsonPath("$.name").value("Test Name"));
    }

    @Test
    void shouldReturn400WhenEmailIsBlank() throws Exception {
        String json = """
                {
                  "email": "   ",
                  "login": "testLogin",
                  "name": "Test Name",
                  "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("электронная почта не может быть пустой"));
    }

    @Test
    void shouldReturn409WhenEmailIsDuplicated() throws Exception {
        String firstUser = """
                {
                  "email": "same@mail.ru",
                  "login": "user1",
                  "name": "User One",
                  "birthday": "2000-01-01"
                }
                """;
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstUser));

        String secondUser = """
                {
                  "email": "same@mail.ru",
                  "login": "user2",
                  "name": "User Two",
                  "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondUser))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Этот имейл уже используется"));
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() throws Exception {
        String json = """
                {
                  "email": "test@mail.ru",
                  "login": "myLogin",
                  "name": "",
                  "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("myLogin")); // Имя стало логином
    }

    @Test
    void shouldReturn400WhenbirthdayIsInFuture() throws Exception {
        String json = """
                {
                  "email": "test@mail.ru",
                  "login": "testLogin",
                  "name": "Test",
                  "birthday": "2099-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("дата рождения не может быть в будущем"));
    }

    @Test
    void shouldReturn200WhenUpdateExistingUser() throws Exception {
        String createJson = """
                {
                  "email": "test@mail.ru",
                  "login": "testLogin",
                  "name": "Test Name",
                  "birthday": "2000-01-01"
                }
                """;
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson));

        String updateJson = """
                {
                  "id": 1,
                  "email": "new@mail.ru",
                  "login": "newLogin",
                  "name": "New Name",
                  "birthday": "1990-05-05"
                }
                """;

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@mail.ru"))
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void shouldReturn404WhenUpdateNonExistentUser() throws Exception {
        String updateJson = """
                {
                  "id": 999,
                  "email": "ghost@mail.ru",
                  "login": "ghost",
                  "name": "Ghost",
                  "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User c id 999 не найден"));
    }

    @Test
    void shouldReturn400WhenUpdateIdIsNull() throws Exception {
        String updateJson = """
                {
                  "email": "test@mail.ru",
                  "login": "testLogin",
                  "name": "Test",
                  "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Id должен быть указан"));
    }

    @Test
    void shouldReturn409WhenUpdateEmailIsDuplicated() throws Exception {
        String user1 = """
                {"email": "one@mail.ru", "login": "user1", "name": "One", "birthday": "2000-01-01"}
                """;
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(user1));

        String user2 = """
                {"email": "two@mail.ru", "login": "user2", "name": "Two", "birthday": "2000-01-01"}
                """;
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(user2));

        String updateJson = """
                {
                  "id": 2,
                  "email": "one@mail.ru",
                  "login": "user2",
                  "name": "Two",
                  "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Этот имейл уже используется"));
    }
}