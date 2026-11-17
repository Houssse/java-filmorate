package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    void create(User user);

    void update(User user);

    void AllDeleteUsers();

    boolean emailExists(String email);

    Long getNextId();

    User getById(Long id);
}
