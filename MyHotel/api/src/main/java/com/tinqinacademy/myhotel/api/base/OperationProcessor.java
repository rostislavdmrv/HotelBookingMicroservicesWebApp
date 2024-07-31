package com.tinqinacademy.myhotel.api.base;

import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import io.vavr.control.Either;


public interface OperationProcessor <I extends OperationInput, O extends OperationOutput>{

    Either<ErrorWrapper, O> process(I input);
}
