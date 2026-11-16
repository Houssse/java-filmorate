package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public Collection<User> getUsers();

    public User create(User user);

    public User update(User user);

    public void delete(User user);
}
