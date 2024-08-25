package com.tinqinacademy.myhotel.api.validations.dateofbirth;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DateOfBirthValidator implements ConstraintValidator<DateOfBirthValidation, LocalDate> {
    private static final int MIN_REGISTRATION_AGE = 18;

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        return date == null || ChronoUnit.YEARS.between(date, LocalDate.now()) >= MIN_REGISTRATION_AGE;
    }

}
