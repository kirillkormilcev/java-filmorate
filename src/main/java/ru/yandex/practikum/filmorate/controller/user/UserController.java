package ru.yandex.practikum.filmorate.controller.user;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.model.user.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
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
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (userValidation(user)) {
            users.put(user.getId(), user);
        }
        return user;
    }

    /** проверка полей добавляемого/обновляемого пользователя */
    private boolean userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new UserValidationException("В логине присутствуют пробелы.");
        }
        if (user.getName().trim().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new UserValidationException("Не корректная дата рождения.");
        }
        for (User userAvailable : users.values()) {
            if (user.getEmail().equals(userAvailable.getEmail())) {
                if (userAvailable.getId() == user.getId()) {
                    return true;
                } else {
                    throw new UserValidationException("Пользователь с такой почтой названием уже зарегистрирован в базе.");
                }
            }
        }
        return true;
    }
}
