package com.tinqinacademy.myhotel.rest.exceptions;

import com.tinqinacademy.myhotel.api.interfaces.exception.ErrorService;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.core.exceptions.TestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorService errorService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions( MethodArgumentNotValidException ex) {
        ErrorWrapper errorWrappers = errorService.errorHandler(ex);
        return new ResponseEntity<>(errorWrappers.getErrors(), errorWrappers.getErrorHttpCode());
    }



    @ExceptionHandler(TestException.class)
    public ResponseEntity<Object> handleTestException(TestException ex) {
        return new ResponseEntity<>("Exception " + ex.getMessage(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex){
        String response = ex.getMessage();
        if (ex.getCause()!=null)
            response+="\nCause: "+ex.getCause();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



}
