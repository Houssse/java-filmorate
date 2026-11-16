package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    void create(User user);

    void update(User user);

    void AllDeleteUsers();

    boolean emailExists (String email);

    Long getNextId();

    User getUser(Long id);
}
