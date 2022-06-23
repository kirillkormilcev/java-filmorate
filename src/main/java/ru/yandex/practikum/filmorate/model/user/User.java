package ru.yandex.practikum.filmorate.model.user;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
public class User implements Serializable {
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
}
