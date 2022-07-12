package ru.yandex.practikum.filmorate.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practikum.filmorate.model.DataType;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * класс пользователя
 */
@EqualsAndHashCode
@Data
@Builder
public class User{
    private long id;
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
    private long friendsCount;
}
