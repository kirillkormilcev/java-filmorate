package ru.yandex.practikum.filmorate.model.film;

import lombok.*;
import ru.yandex.practikum.filmorate.model.DataType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

/**
 * класс фильма
 */
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
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
    private int likesRating;
    private MPA MPA;
    private final Set<Genre> genres;
}
