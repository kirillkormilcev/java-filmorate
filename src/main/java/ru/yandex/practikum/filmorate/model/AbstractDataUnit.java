package ru.yandex.practikum.filmorate.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/** обобщающий класс экземпляра данных */
@Data
public abstract class AbstractDataUnit implements Serializable {
    protected int id;
    protected String name;
    protected String description;
    protected LocalDate releaseDate;
    protected int duration;
    protected String email;
    protected String login;
    protected LocalDate birthday;
    protected String dataType;
}
