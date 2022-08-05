package ru.yandex.practikum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practikum.filmorate.model.DataType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * класс фильма
 */
@EqualsAndHashCode(of="id")
@Data
@Builder
public class Film {
    private long id;
    @NotNull
    @NotBlank
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
    private final DataType dataType = DataType.FILM;
    private long likesRating;
    private final long filmGenresId;
    private final long MPARatingId;
}
