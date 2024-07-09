package com.tinqinacademy.myhotel.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ErrorService {
    ErrorWrapper errorHandler(MethodArgumentNotValidException ex);
}
