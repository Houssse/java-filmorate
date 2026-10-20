package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public abstract class ApplicationController {

    protected int getNextId(Map<Integer, ?> map) {
        int currentMaxId = map.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
