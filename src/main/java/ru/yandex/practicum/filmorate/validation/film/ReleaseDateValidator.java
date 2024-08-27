package ru.yandex.practicum.filmorate.validation.film;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<DateNotEarly, LocalDate> {
    private LocalDate comparisonDate;

    @Override
    public void initialize(DateNotEarly constraintAnnotation) {
        this.comparisonDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return false;
        }
        return !localDate.isBefore(comparisonDate);
    }
}
