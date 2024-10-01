package ru.yandex.practicum.filmorate.model.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private int id;
    @Email(regexp = "^[A-z0-9._%+-]+@[a-z0-9-]+.+.[a-z]{2,4}$", message = "Incorrect email format")
    private String email;
    @NotEmpty
    @NotNull
    @Pattern(regexp = "\\S+", message = "Login should contain no whitespaces")
    private String login;
    private String name;
    @Past(message = "The birthday should be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public String getName() {
        return (Objects.isNull(name) || name.isEmpty()) ? login : name;
    }
}
