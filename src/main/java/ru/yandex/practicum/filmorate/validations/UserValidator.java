package ru.yandex.practicum.filmorate.validations;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    public static boolean checkUser(User user) {
        checkEmail(user);
        checkLogin(user);
        checkBirthDay(user);

        return true;
    }

    public static void checkId(User user) {
        if (user.getId() == null ) {
            throw new ValidationException("Id должен быть указан");
        }
    }

    public static void checkEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("электронная почта не может быть пустой");
        }

        long atCount = user.getEmail().chars().filter(ch -> ch == '@').count();

        if (atCount == 0) {
            throw new ValidationException("Email должен содержать символ @");
        }

        if (atCount > 1) {
            throw new ValidationException("Email не может содержать больше одного символа @");
        }
    }

    public static void checkLogin(User user) {
        if (hasWhitespace(user.getLogin())) {
            throw new ValidationException("логин не может содержать пробелы");
        }

        if (user.getLogin().isBlank()) {
            throw new ValidationException("логин не может быть пустым");
        }
    }

    public static void checkName(User user) {
        if (hasWhitespace(user.getName())) {
            throw new ValidationException("имя не может содержать пробелы");
        }

    }

    public static void checkBirthDay(User user) {
        if (user.getBirthDay() == null) {
            throw new ValidationException("Дата рождения не может быть пустой");
        }

        LocalDate today = LocalDate.now();

        if (user.getBirthDay().isAfter(today)) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
    }

    private static boolean hasWhitespace(String str) {
        return str.chars().anyMatch(Character::isWhitespace);
    }
}
