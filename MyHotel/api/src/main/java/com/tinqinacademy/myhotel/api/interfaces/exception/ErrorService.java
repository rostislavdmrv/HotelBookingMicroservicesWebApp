package com.tinqinacademy.myhotel.api.interfaces.exception;

import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ErrorService {
    ErrorWrapper errorHandler(MethodArgumentNotValidException ex);
}
