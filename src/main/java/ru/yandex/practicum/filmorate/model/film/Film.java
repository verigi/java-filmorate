package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validation.DateNotEarly;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    private static final String THE_EARLIEST_RELEASE_DATE = "1895-12-28";
    private int id;
    @NotNull
    @NotEmpty
    private String name;
    @Size(max = 200, message = "Description should be less than 200 symbols")
    private String description;
    @DateNotEarly(value = THE_EARLIEST_RELEASE_DATE, message = "Release date should not be early than 28.12.1895")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;
}
