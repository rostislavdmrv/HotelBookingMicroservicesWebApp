package com.tinqinacademy.myhotel.api.errors;

import com.tinqinacademy.myhotel.api.exceptions.NotAvailableException;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.models.error.ErrorsResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Component
public class ErrorHandler {
    public ErrorWrapper handleErrors(Throwable throwable) {
        List<ErrorsResponse> errors = new ArrayList<>();

        HttpStatus status = Match(throwable).of(

                Case($(instanceOf(MethodArgumentNotValidException.class)), ex -> {
                    ex.getBindingResult().getFieldErrors()
                            .forEach(error ->
                                    errors.add(ErrorsResponse.builder()
                                            .field(error.getField())
                                            .message(error.getDefaultMessage())
                                            .build()));
                    return HttpStatus.BAD_REQUEST;
                }),

                Case($(instanceOf(ConstraintViolationException.class)), ex -> {
                    ex.getConstraintViolations()
                            .forEach(violation -> errors.add(ErrorsResponse.builder()
                                    .field(violation.getPropertyPath().toString())
                                    .message(violation.getMessage())
                                    .build()));
                    return HttpStatus.BAD_REQUEST;
                }),

                Case($(instanceOf(NotAvailableException.class)), ex -> {
                    errors.add(ErrorsResponse.builder().message(ex.getMessage()).build());
                    return HttpStatus.FORBIDDEN;
                }),

                Case($(instanceOf(ResourceNotFoundException.class)), ex -> {
                    errors.add(ErrorsResponse.builder().message(ex.getMessage()).build());
                    return HttpStatus.NOT_FOUND;
                }),

                Case($(), ex -> {
                    errors.add(ErrorsResponse.builder().message(ex.getMessage()).build());
                    return HttpStatus.INTERNAL_SERVER_ERROR;
                })
        );

        return ErrorWrapper.builder()
                .errors(errors)
                .errorHttpCode(status)
                .build();
    }
}
