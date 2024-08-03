package com.tinqinacademy.myhotel.api.exceptions;

import com.tinqinacademy.myhotel.api.models.errors.ErrorsResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HotelValidationException extends RuntimeException {
    private final List<ErrorsResponse> violations;

    public HotelValidationException(List<ErrorsResponse> violations) {
        this.violations = violations;
    }
}
