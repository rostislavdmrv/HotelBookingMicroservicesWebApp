package com.tinqinacademy.myhotel.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorService errorServiceImpl;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions( MethodArgumentNotValidException ex) {
        ErrorWrapper errorWrappers = errorServiceImpl.errorHandler(ex);
        return new ResponseEntity<>(errorWrappers.getErrors(), errorWrappers.getErrorCode());
    }



    @ExceptionHandler(TestException.class)
    public ResponseEntity<Object> handleTestException(TestException ex) {
        return new ResponseEntity<>("Proba " + ex.getMessage(), HttpStatus.BAD_REQUEST);

    }



}
