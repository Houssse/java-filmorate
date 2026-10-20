package ru.yandex.practicum.filmorate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validations.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController extends ApplicationController{
    Map<Long, User> users = new HashMap<Long, User>();

    @GetMapping
    public Collection<User> index(){
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user){
        if (UserValidator.checkUser(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            } else {
                UserValidator.checkName(user);
            }
            user.setId(user.getId());
            users.put(user.getId(), user);
        }
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user){
        User oldUser = users.get(user.getId());
        return users.put(user.getId(), user);
    }
}
