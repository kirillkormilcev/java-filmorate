package ru.yandex.practikum.filmorate.model.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practikum.filmorate.model.AbstractDataUnit;
import ru.yandex.practikum.filmorate.model.DataType;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * класс пользователя
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class User extends AbstractDataUnit {
    private int id;
    @NotNull
    @NotBlank
    @Email
    private final String email;
    @NotNull
    @NotBlank
    private final String login;
    private String name;
    private LocalDate birthday;
    private final DataType dataType = DataType.USER;
}
