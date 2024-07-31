package com.tinqinacademy.myhotel.core.processors.base;

import com.tinqinacademy.myhotel.api.base.OperationInput;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;

import java.util.Set;

public abstract class BaseOperationProcessor {

    protected final ConversionService conversionService;
    private final Validator validator;

    protected BaseOperationProcessor(ConversionService conversionService, Validator validator) {
        this.conversionService = conversionService;
        this.validator = validator;
    }

    protected void validateInput(OperationInput input) {
        Set<ConstraintViolation<OperationInput>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
