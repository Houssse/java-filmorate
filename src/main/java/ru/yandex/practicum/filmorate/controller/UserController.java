package ru.yandex.practicum.filmorate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController extends ApplicationController{
    Map<Integer, User> users = new HashMap<Integer, User>();

    @GetMapping
    public Collection<User> index(){
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user){
        user.setId(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user){
        User oldUser = users.get(user.getId());
        return users.put(user.getId(), user);
    }
}
