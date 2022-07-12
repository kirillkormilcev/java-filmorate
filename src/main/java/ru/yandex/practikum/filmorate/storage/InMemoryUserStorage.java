package ru.yandex.practikum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practikum.filmorate.model.user.User;

import java.util.*;

@Component
@Slf4j
@Getter
public class InMemoryUserStorage implements UserStorage{
    private final Map<Long, User> users = new LinkedHashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    @Override
    public List<User> getListOfUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(idGenerator.getId());
        users.put(user.getId(), user);
        log.info("Получен POST запрос к эндпоинту /{}s, успешно обработан.\n" +
                        "В базу добавлен пользователь: '{}' с id: '{}'.", user.getDataType().toString().toLowerCase(Locale.ROOT),
                user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Получен PUT запрос к эндпоинту: /{}s, успешно обработан.\n" +
                        "В базе обновлен пользователь: '{}' с id: '{}'.", user.getDataType().toString().toLowerCase(Locale.ROOT),
                user.getName(), user.getId());
        return user;
    }
}
