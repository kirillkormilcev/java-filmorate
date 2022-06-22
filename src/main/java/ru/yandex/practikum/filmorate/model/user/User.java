package ru.yandex.practikum.filmorate.model.user;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotNull
    @Email
    private final String email;
    @NotNull
    @NotBlank
    private final String login;
    private String name;
    private LocalDate birthday;
}
