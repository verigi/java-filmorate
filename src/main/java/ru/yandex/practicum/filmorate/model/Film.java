package ru.yandex.practicum.filmorate.model;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validation.film.DateNotEarly;

import java.time.LocalDate;


/**
 {
 id: 1;
 name: "some";
 description: "some text";
 releaseDate: "1996-03-29";
 duration: 120;
 }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    private static final String THE_EARLIEST_RELEASE_DATE = "1895-12-28";
    private int id;
    @NonNull
    @NotEmpty
    private String name;
    @Size(max = 200, message = "Description should be less than 200 symbols")
    private String description;
    @DateNotEarly(value = THE_EARLIEST_RELEASE_DATE, message = "Release date must not be early than 28-12-1895")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive
    private int duration;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
