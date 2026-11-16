package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;

    // ==================== CREATE ====================

    @Test
    void shouldCreateUserSuccessfully() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userStorage.emailExists(anyString())).thenReturn(false);

        when(userStorage.getNextId()).thenReturn(1L);


        User created = userService.createUser(user);


        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("test@mail.ru", created.getEmail());
        assertEquals("Test Name", created.getName());

        verify(userStorage, times(1)).create(any(User.class));
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("myLogin");
        user.setName("   ");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userStorage.emailExists(anyString())).thenReturn(false);
        when(userStorage.getNextId()).thenReturn(1L);

        User created = userService.createUser(user);

        assertEquals("myLogin", created.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsDuplicatedOnCreate() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userStorage.emailExists("test@mail.ru")).thenReturn(true);

        assertThrows(DuplicatedDataException.class, () -> userService.createUser(user));

        verify(userStorage, never()).create(any());
    }

    // ==================== UPDATE ====================

    @Test
    void shouldUpdateUserSuccessfully() {
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setEmail("old@mail.ru");
        oldUser.setLogin("oldLogin");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("new@mail.ru");
        newUser.setLogin("newLogin");

        when(userStorage.getById(1L)).thenReturn(oldUser);

        when(userStorage.emailExists("new@mail.ru")).thenReturn(false);

        User updated = userService.updateUser(newUser);

        assertEquals("new@mail.ru", updated.getEmail());
        assertEquals("newLogin", updated.getLogin());

        verify(userStorage, times(1)).update(oldUser);
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentUser() {
        User newUser = new User();
        newUser.setId(999L);

        when(userStorage.getById(999L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.updateUser(newUser));
        verify(userStorage, never()).update(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdateWithDuplicatedEmail() {
        User oldUser = new User();
        oldUser.setId(1L);

        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("taken@mail.ru");

        when(userStorage.getById(1L)).thenReturn(oldUser);

        when(userStorage.emailExists("taken@mail.ru")).thenReturn(true);

        assertThrows(DuplicatedDataException.class, () -> userService.updateUser(newUser));
        verify(userStorage, never()).update(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdateIdIsNull() {
        User newUser = new User();
        newUser.setId(null);

        assertThrows(ValidationException.class, () -> userService.updateUser(newUser));
    }
}