package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("Вася");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @Test
    void index_returnsUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user@mail.ru"));
    }

    @Test
    void create_returnsUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(sampleUser());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_withDuplicateEmail_returnsConflict() throws Exception {
        when(userService.createUser(any(User.class)))
                .thenThrow(new DuplicatedDataException("Этот имейл уже используется"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Этот имейл уже используется"));
    }

    @Test
    void create_withInvalidEmail_returnsBadRequest() throws Exception {
        when(userService.createUser(any(User.class)))
                .thenThrow(new ValidationException("Email должен содержать символ @"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"no-at\",\"login\":\"user\",\"birthday\":\"2000-01-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email должен содержать символ @"));
    }

    @Test
    void update_returnsUser() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(sampleUser());

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_withoutId_returnsBadRequest() throws Exception {
        when(userService.updateUser(any(User.class)))
                .thenThrow(new ValidationException("Id должен быть указан"));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.ru\",\"login\":\"user\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Id должен быть указан"));
    }

    @Test
    void getFriends_returnsFriends() throws Exception {
        when(userService.getAllFriends(1L)).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getFriends_whenUserNotFound_returnsNotFound() throws Exception {
        when(userService.getAllFriends(99L))
                .thenThrow(new NotFoundException("User с id 99 не найден"));

        mockMvc.perform(get("/users/99/friends"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFriend_callsService() throws Exception {
        doNothing().when(userService).addFriend(1L, 2L);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());

        verify(userService).addFriend(1L, 2L);
    }

    @Test
    void addFriend_whenFriendNotFound_returnsNotFound() throws Exception {
        doThrow(new NotFoundException("User с id 2 не найден"))
                .when(userService).addFriend(1L, 2L);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFriend_callsService() throws Exception {
        doNothing().when(userService).deleteFriend(1L, 2L);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());

        verify(userService).deleteFriend(1L, 2L);
    }

    @Test
    void getCommonFriends_returnsList() throws Exception {
        when(userService.getCommonFriends(1L, 2L)).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(userService).getCommonFriends(1L, 2L);
    }
}