package ru.yandex.practikum.filmorate.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practikum.filmorate.controller.AbstractController;
import ru.yandex.practikum.filmorate.model.user.User;

import java.time.LocalDate;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends AbstractController<User> {

    /**
     * проверка полей добавляемого/обновляемого пользователя
     */
    protected boolean dataValidation(User user) {
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
        for (User userAvailable : storage.values()) {
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
