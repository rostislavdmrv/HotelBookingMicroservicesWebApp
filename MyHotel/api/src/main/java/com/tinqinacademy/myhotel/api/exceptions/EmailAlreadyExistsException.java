package com.tinqinacademy.myhotel.api.exceptions;

import com.tinqinacademy.myhotel.api.exceptions.messages.Messages;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAlreadyExistsException extends RuntimeException{
    private final String message;

    public EmailAlreadyExistsException() {
        this.message = Messages.EMAIL_ALREADY_EXISTS;
    }
}
