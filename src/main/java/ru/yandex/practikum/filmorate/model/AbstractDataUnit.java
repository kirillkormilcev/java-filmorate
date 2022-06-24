package ru.yandex.practikum.filmorate.model;

import lombok.Data;

import java.io.Serializable;

/**
 * обобщающий класс экземпляра данных
 */
@Data
public abstract class AbstractDataUnit implements Serializable {
    protected int id; /* общие поля */
    protected String name;
    protected DataType dataType;
}
