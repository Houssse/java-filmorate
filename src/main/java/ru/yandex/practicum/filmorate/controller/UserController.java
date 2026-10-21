package ru.yandex.practicum.filmorate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validations.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController extends ApplicationController {
    Map<Long, User> users = new HashMap<Long, User>();

    @GetMapping
    public Collection<User> index() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (UserValidator.checkUser(user)) {
            boolean emailExists = users.values().stream()
                    .anyMatch(u -> u.getEmail().equals(user.getEmail()));

            if (emailExists) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            } else {
                UserValidator.checkName(user);
            }
            user.setId(getNextId(users));
            users.put(user.getId(), user);
        }
        return user;
    }

    //В тз не сказано какие поля можно обновлять поэтому обновляем любое
    @PutMapping
    public User update(@RequestBody User newUser) {
        UserValidator.checkId(newUser);

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("User c id " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null) {
            UserValidator.checkEmail(newUser);
            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            UserValidator.checkLogin(newUser);
            oldUser.setLogin(newUser.getLogin());
        }

        if (newUser.getName() != null) {
            UserValidator.checkName(newUser);
            oldUser.setName(newUser.getName());
        }

        if (newUser.getBirthDay() != null) {
            UserValidator.checkBirthDay(newUser);
            oldUser.setBirthDay(newUser.getBirthDay());
        }

        return oldUser;
    }
}
