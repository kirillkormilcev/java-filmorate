package ru.yandex.practikum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
public class Film implements Serializable {
    @Builder.Default
    private int id = 0;
    @NotNull
    @NotBlank
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
}
