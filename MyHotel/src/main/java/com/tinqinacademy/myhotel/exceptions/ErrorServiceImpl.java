package com.tinqinacademy.myhotel.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

@Component
public class ErrorServiceImpl  implements ErrorService{

    public ErrorWrapper errorHandler(MethodArgumentNotValidException ex) {
        List<ErrorsResponse> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(
                        ErrorsResponse.builder()
                                .field(error.getField())
                                .message(error.getDefaultMessage())
                                .build()));

        return ErrorWrapper.builder()
                .errors(errors)
                .errorCode(HttpStatus.BAD_REQUEST).build();
    }
}
