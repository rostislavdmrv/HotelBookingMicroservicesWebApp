package com.tinqinacademy.myhotel.core.processors.base;

import com.tinqinacademy.myhotel.api.base.OperationInput;
import com.tinqinacademy.myhotel.api.base.OperationOutput;
import com.tinqinacademy.myhotel.api.exceptions.HotelValidationException;
import com.tinqinacademy.myhotel.api.models.errors.ErrorsResponse;
import com.tinqinacademy.myhotel.core.errorhandler.ErrorHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class BaseOperationProcessor<I extends OperationInput, O extends OperationOutput> {

    protected final ConversionService conversionService;
    protected final Validator validator;
    protected final ErrorHandler errorHandler;

    protected BaseOperationProcessor(ConversionService conversionService, Validator validator, ErrorHandler errorHandler) {
        this.conversionService = conversionService;
        this.validator = validator;
        this.errorHandler = errorHandler;
    }


    protected void validateInput(OperationInput input) {
        Set<ConstraintViolation<OperationInput>> violations = validator.validate(input);

        if (!violations.isEmpty()) {
            List<ErrorsResponse> errors = buildErrors(violations);

            throw new HotelValidationException(errors);
        }
    }

    private List<ErrorsResponse> buildErrors(Set<ConstraintViolation<OperationInput>> violations) {
        List<ErrorsResponse> errors = new ArrayList<>();
        for (ConstraintViolation<OperationInput> violation : violations) {
            errors.add(ErrorsResponse.builder()
                    .field(violation.getPropertyPath().toString())
                    .message(violation.getMessage())
                    .build());
        }
        return errors;
    }
}

