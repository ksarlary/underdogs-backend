package org.underdogs.users.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    private int minimumAge;

    @Override
    public void initialize(Adult constraintAnnotation) {
        this.minimumAge = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate minimumBirthDate = today.minusYears(minimumAge);

        return !birthDate.isAfter(minimumBirthDate);
    }
}
