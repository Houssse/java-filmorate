package ru.yandex.practicum.filmorate.validations;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    @Test
    void shouldThrowWhenEmailIsNull() {
        User user = new User();
        user.setEmail(null);
        assertThrows(ValidationException.class, () -> UserValidator.checkEmail(user));
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        User user = new User();
        user.setEmail("   ");
        assertThrows(ValidationException.class, () -> UserValidator.checkEmail(user));
    }

    @Test
    void shouldThrowWhenEmailHasNoAt() {
        User user = new User();
        user.setEmail("testmail.ru");
        assertThrows(ValidationException.class, () -> UserValidator.checkEmail(user));
    }

    @Test
    void shouldThrowWhenEmailHasTwoAt() {
        User user = new User();
        user.setEmail("test@@mail.ru");
        assertThrows(ValidationException.class, () -> UserValidator.checkEmail(user));
    }

    @Test
    void shouldNotThrowWhenEmailIsValid() {
        User user = new User();
        user.setEmail("test@mail.ru");
        assertDoesNotThrow(() -> UserValidator.checkEmail(user));
    }

    @Test
    void shouldThrowWhenLoginIsBlank() {
        User user = new User();
        user.setLogin("   ");
        assertThrows(ValidationException.class, () -> UserValidator.checkLogin(user));
    }

    @Test
    void shouldThrowWhenLoginHasWhitespace() {
        User user = new User();
        user.setLogin("test login");
        assertThrows(ValidationException.class, () -> UserValidator.checkLogin(user));
    }

    @Test
    void shouldNotThrowWhenLoginIsValid() {
        User user = new User();
        user.setLogin("testLogin");
        assertDoesNotThrow(() -> UserValidator.checkLogin(user));
    }

    @Test
    void shouldThrowWhenNameHasWhitespace() {
        User user = new User();
        user.setName("test name");
        assertThrows(ValidationException.class, () -> UserValidator.checkName(user));
    }

    @Test
    void shouldNotThrowWhenNameIsValid() {
        User user = new User();
        user.setName("testName");
        assertDoesNotThrow(() -> UserValidator.checkName(user));
    }

    @Test
    void shouldThrowWhenBirthDayIsNull() {
        User user = new User();
        user.setBirthDay(null);
        assertThrows(ValidationException.class, () -> UserValidator.checkBirthDay(user));
    }

    @Test
    void shouldThrowWhenBirthDayIsInFuture() {
        User user = new User();
        user.setBirthDay(LocalDate.now().plusDays(1)); // завтра
        assertThrows(ValidationException.class, () -> UserValidator.checkBirthDay(user));
    }

    @Test
    void shouldNotThrowWhenBirthDayIsToday() {
        User user = new User();
        user.setBirthDay(LocalDate.now()); // сегодня (граница — не будущее)
        assertDoesNotThrow(() -> UserValidator.checkBirthDay(user));
    }

    @Test
    void shouldNotThrowWhenBirthDayIsInPast() {
        User user = new User();
        user.setBirthDay(LocalDate.of(2000, 1, 1));
        assertDoesNotThrow(() -> UserValidator.checkBirthDay(user));
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        User user = new User();
        user.setId(null);
        assertThrows(ValidationException.class, () -> UserValidator.checkId(user));
    }

    @Test
    void shouldNotThrowWhenIdIsPresent() {
        User user = new User();
        user.setId(1L);
        assertDoesNotThrow(() -> UserValidator.checkId(user));
    }
}