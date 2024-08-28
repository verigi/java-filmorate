package model;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

public class UserTest {
    private User defaultUser = User.builder()
            .email("validator_test@yandex.ru")
            .name("Validation test")
            .login("Validator_2000")
            .birthday(LocalDate.of(2000, 1, 1))
            .build();

    private final String[] incorrectLogins = {"Va lidator", "", null, " "};
    private final String[] correctLogins = {"Validator", "Validator96", "Validator_96"};
    private final String[] incorrectEmails = {"e mail@yandex.ru", "e?mail@yandex.ru", "email@yandex.ru.com"};
    private final String[] correctEmails = {"email_96@yandex.ru", "email1@yandex.ru"};
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("Creating new user")
    public void shouldCreateUser() {
        Set<ConstraintViolation<User>> violation = validator.validate(defaultUser);
        Assertions.assertTrue(violation.isEmpty());
    }

    @Test
    @DisplayName("Incorrect email check")
    public void shouldNotCreateUserWithIncorrectEmail() {
        Arrays.stream(incorrectEmails).forEach((email) -> {
            User user = User.builder()
                    .email(email)
                    .build();

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            Assertions.assertFalse(violations.isEmpty());
        });
    }

    @Test
    @DisplayName("Correct email check")
    public void shouldCreateUserWithCorrectEmail() {
        Arrays.stream(correctEmails).forEach((email) -> {
            User user = User.builder()
                    .email(email)
                    .login("Test")
                    .build();

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            Assertions.assertTrue(violations.isEmpty());
        });
    }

    @Test
    @DisplayName("Incorrect login check")
    public void shouldNotCreateUserWithIncorrectLogin() {
        Arrays.stream(incorrectLogins).forEach((login) -> {
            User user = User.builder()
                    .login(login)
                    .build();
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            Assertions.assertFalse(violations.isEmpty());
        });
    }

    @Test
    @DisplayName("Correct login check")
    public void shouldCreateUserWithCorrectLogin() {
        Arrays.stream(correctLogins).forEach((login) -> {
            User user = User.builder()
                    .login(login)
                    .build();
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            Assertions.assertTrue(violations.isEmpty());
        });
    }

    @Test
    @DisplayName("Incorrect birthday check")
    public void shouldNotCreateUserWithCorrectBirthday() {
        User user  = User.builder()
                .email("test@yandex.ru")
                .login("Test")
                .birthday(LocalDate.of(2025,1,1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Correct bithday check")
    public void shouldCreateUserWithCorrectBirthday() {
        User user  = User.builder()
                .email("test@yandex.ru")
                .login("Test")
                .birthday(LocalDate.of(2000,1,1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertTrue(violations.isEmpty());
    }
}
