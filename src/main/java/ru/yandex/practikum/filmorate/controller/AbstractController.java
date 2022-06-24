package ru.yandex.practikum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.model.AbstractDataUnit;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@Getter
public abstract class AbstractController <T extends AbstractDataUnit>{

    protected Map<Integer, T> storage = new LinkedHashMap<>();
    protected String uri = "";
    protected IdGenerator idGenerator = new IdGenerator();

    @GetMapping
    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

    @PostMapping
    public T addData(@Valid @RequestBody T data) {
        if (dataValidation(data)) {
            data.setId(idGenerator.getId());
            storage.put(data.getId(), data);
            log.info("Получен POST запрос к эндпоинту '{}', успешно обработан.\n" +
                    "В базу добавлен пользователь: '{}' с id: '{}'.", uri, data.getLogin(), data.getId());
        }
        return data;
    }

    @PutMapping
    public T updateData(@Valid @RequestBody T data) {
        if (dataValidation(data)) {
            storage.put(data.getId(), data);
            log.info("Получен PUT запрос к эндпоинту: '{}', успешно обработан.\n" +
                    "В базе обновлен пользователь: '{}' с id: '{}'.", uri, data.getLogin(), data.getId());
        }
        return data;
    }

    protected abstract boolean dataValidation(T data);
}
