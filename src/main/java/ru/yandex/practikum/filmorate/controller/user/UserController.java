package ru.yandex.practikum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.model.user.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (userValidation(user)) {
            user.setId(UserId.getId());
            users.put(user.getId(), user);
            log.info("Получен POST запрос к эндпоинту: /users, успешно обработан.\n" +
                    "В базу добавлен пользователь: '{}' с id: '{}'." , user.getLogin(), user.getId());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (userValidation(user)) {
            users.put(user.getId(), user);
            log.info("Получен PUT запрос к эндпоинту: /users, успешно обработан.\n" +
                    "В базе обновлен пользователь: '{}' с id: '{}'." , user.getLogin(), user.getId());
        }
        return user;
    }

    /** проверка полей добавляемого/обновляемого пользователя */
    private boolean userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Попытка добавить или обновить пользователя с логином: '{}', содержащего пробелы.", user.getLogin());
            throw new UserValidationException("В логине присутствуют пробелы.");
        }
        if (user.getName().trim().isBlank()) {
            log.info("Пользователю с логином: '{}' назначено аналогичное имя.", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка добавить или обновить пользователя с логином: '{}' с некорректной датой рождения: '{}'.",
                    user.getLogin(), user.getBirthday());
            throw new UserValidationException("Не корректная дата рождения.");
        }
        for (User userAvailable : users.values()) {
            if (user.getEmail().equals(userAvailable.getEmail())) {
                if (userAvailable.getId() == user.getId()) {
                    return true;
                } else {
                    log.warn("Попытка добавить или обновить пользователя с логином: '{}' с уже существующей в базе почтой: '{}'.",
                            user.getLogin(), user.getEmail());
                    throw new UserValidationException("Пользователь с такой почтой уже зарегистрирован в базе.");
                }
            }
        }
        return true;
    }
}
