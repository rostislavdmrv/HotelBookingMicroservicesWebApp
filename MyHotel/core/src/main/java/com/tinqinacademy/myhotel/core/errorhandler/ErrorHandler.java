package com.tinqinacademy.myhotel.core.errorhandler;

import com.tinqinacademy.myhotel.api.exceptions.*;
import com.tinqinacademy.myhotel.api.models.errors.ErrorWrapper;
import com.tinqinacademy.myhotel.api.models.errors.ErrorsResponse;
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

        HttpStatus status = determineHttpStatusAndFillErrors(throwable, errors);

        return ErrorWrapper.builder()
                .errors(errors)
                .errorHttpCode(status)
                .build();
    }

    private HttpStatus determineHttpStatusAndFillErrors(Throwable throwable, List<ErrorsResponse> errors) {
        return Match(throwable).of(
                Case($(instanceOf(MethodArgumentNotValidException.class)), ex -> handleMethodArgumentNotValidException((MethodArgumentNotValidException) ex, errors)),
                Case($(instanceOf(HotelValidationException.class)), ex -> handleHotelValidationException((HotelValidationException) ex, errors)),
                Case($(instanceOf(NotAvailableException.class)), ex -> handleNotAvailableException((NotAvailableException) ex, errors)),
                Case($(instanceOf(ResourceNotFoundException.class)), ex -> handleResourceNotFoundException((ResourceNotFoundException) ex, errors)),
                Case($(instanceOf(UserNotFoundException.class)), ex -> handleUserNotFoundException((UserNotFoundException) ex, errors)),
                Case($(instanceOf(EmailAlreadyExistsException.class)), ex -> handleEmailAlreadyExistsException((EmailAlreadyExistsException) ex, errors)),
                Case($(instanceOf(RoomNoAlreadyExistsException.class)), ex -> handleRoomNoAlreadyExistsException((RoomNoAlreadyExistsException) ex, errors)),
                Case($(instanceOf(NoMatchException.class)), ex -> handleNoMatchException((NoMatchException) ex, errors)),
                Case($(), ex -> handleGenericException(ex, errors))
        );
    }

    private HttpStatus handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, List<ErrorsResponse> errors) {
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.add(createErrorResponse(error.getField(), error.getDefaultMessage())));
        return HttpStatus.BAD_REQUEST;
    }

    private HttpStatus handleHotelValidationException(HotelValidationException ex, List<ErrorsResponse> errors) {
        ex.getViolations().forEach(violation -> errors.add(createErrorResponse(violation.getField(), violation.getMessage())));
        return HttpStatus.BAD_REQUEST;
    }

    private HttpStatus handleNotAvailableException(NotAvailableException ex, List<ErrorsResponse> errors) {
        errors.add(createErrorResponse(null, ex.getMessage()));
        return HttpStatus.FORBIDDEN;
    }

    private HttpStatus handleResourceNotFoundException(ResourceNotFoundException ex, List<ErrorsResponse> errors) {
        errors.add(createErrorResponse(null, ex.getMessage()));
        return HttpStatus.NOT_FOUND;
    }

    private HttpStatus handleUserNotFoundException(UserNotFoundException ex, List<ErrorsResponse> errors) {
        errors.add(createErrorResponse(null, ex.getMessage()));
        return HttpStatus.NOT_FOUND;
    }

    private HttpStatus handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, List<ErrorsResponse> errors) {
        errors.add(createErrorResponse(null, ex.getMessage()));
        return HttpStatus.CONFLICT;
    }

    private HttpStatus handleRoomNoAlreadyExistsException(RoomNoAlreadyExistsException ex, List<ErrorsResponse> errors) {
        errors.add(createErrorResponse(null, ex.getMessage()));
        return HttpStatus.CONFLICT;
    }

    private HttpStatus handleNoMatchException(NoMatchException ex, List<ErrorsResponse> errors) {
        errors.add(createErrorResponse(null, ex.getMessage()));
        return HttpStatus.NOT_FOUND;
    }

    private HttpStatus handleGenericException(Throwable ex, List<ErrorsResponse> errors) {
        errors.add(createErrorResponse(null, ex.getMessage()));
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ErrorsResponse createErrorResponse(String field, String message) {
        return ErrorsResponse.builder()
                .field(field)
                .message(message)
                .build();
    }
}
