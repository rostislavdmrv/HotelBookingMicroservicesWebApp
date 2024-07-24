package com.tinqinacademy.myhotel.api.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotAvailableException extends RuntimeException {
    public NotAvailableException(String message) {
        super(message);
    }
}
