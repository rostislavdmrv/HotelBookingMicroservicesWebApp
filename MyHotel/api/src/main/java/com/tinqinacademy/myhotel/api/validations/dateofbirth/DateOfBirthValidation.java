package com.tinqinacademy.myhotel.api.validations.dateofbirth;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateOfBirthValidator.class)
public @interface DateOfBirthValidation {
    String message() default " ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
