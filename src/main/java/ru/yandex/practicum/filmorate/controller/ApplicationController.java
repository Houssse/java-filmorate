package ru.yandex.practicum.filmorate.controller;

import java.util.Map;

public abstract class ApplicationController {

    protected Long getNextId(Map<Long, ?> map) {
        long currentMaxId = map.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
