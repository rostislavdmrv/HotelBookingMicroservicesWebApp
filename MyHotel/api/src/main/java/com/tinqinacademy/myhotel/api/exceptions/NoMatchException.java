package com.tinqinacademy.myhotel.api.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoMatchException extends RuntimeException{
    public NoMatchException(String message) {
        super(message);
    }
}
