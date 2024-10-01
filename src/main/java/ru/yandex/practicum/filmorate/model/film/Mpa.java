package ru.yandex.practicum.filmorate.model.film;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Mpa {
    private Integer id;
    private String name;
}
