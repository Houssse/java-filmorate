package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validations.UserValidator;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
       return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        log.debug("Создание user {}", user);
        if (UserValidator.checkUser(user)) {

            if (userStorage.emailExists(user.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
//            } else {
//                UserValidator.checkName(user);
            }
            user.setId(userStorage.getNextId());
            userStorage.create(user);
        }
        log.debug("User создан {}", user);
        return user;
    }

    public User updateUser(User newUser) {
        log.debug("Обновление user {}", newUser);

        UserValidator.checkId(newUser);

        User oldUser = userStorage.getById(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("User c id " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null) {
            UserValidator.checkEmail(newUser);

            if (userStorage.emailExists(newUser.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }

            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            UserValidator.checkLogin(newUser);
            oldUser.setLogin(newUser.getLogin());
        }

        if (newUser.getName() != null) {
//            UserValidator.checkName(newUser);
            oldUser.setName(newUser.getName());
        }

        if (newUser.getBirthday() != null) {
            UserValidator.checkBirthDay(newUser);
            oldUser.setBirthday(newUser.getBirthday());
        }

        userStorage.update(oldUser);
        log.debug("Обновлен user {}", oldUser);

        return oldUser;
    }
}
