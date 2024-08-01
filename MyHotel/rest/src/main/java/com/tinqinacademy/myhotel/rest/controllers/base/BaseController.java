package com.tinqinacademy.myhotel.rest.controllers.base;

import com.tinqinacademy.myhotel.api.base.OperationOutput;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected ResponseEntity<?> handle(Either<ErrorWrapper, ? extends OperationOutput> output) {
        if (output.isLeft()) {
            return error(output);
        }
        return new ResponseEntity<>(output.get(), HttpStatus.OK);
    }

    protected ResponseEntity<?> handleWithStatus(Either<ErrorWrapper, ? extends OperationOutput> output, HttpStatus status) {
        if (output.isLeft()) {
            return error(output);
        }
        return new ResponseEntity<>(output.get(), status);
    }

    private ResponseEntity<?> error(Either<ErrorWrapper, ? extends OperationOutput> output) {
        ErrorWrapper errorWrapper = output.getLeft();
        return new ResponseEntity<>(errorWrapper.getErrors(), errorWrapper.getErrorHttpCode());
    }
}
