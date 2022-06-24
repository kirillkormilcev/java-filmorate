package ru.yandex.practikum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/** класс экземпляра данных */
@Data
@Builder
public class StorageData implements Serializable {
    @Builder.Default
    protected int id = 0;
}
