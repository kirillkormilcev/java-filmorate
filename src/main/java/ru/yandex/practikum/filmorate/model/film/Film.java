package ru.yandex.practikum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practikum.filmorate.model.AbstractDataUnit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/** класс фильма */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
public class Film extends AbstractDataUnit {
    @Builder.Default
    private int id = 0;
    @NotNull
    @NotBlank
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
    @Builder.Default
    private final String dataType = "Film";
}
