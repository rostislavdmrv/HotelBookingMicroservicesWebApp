package com.tinqinacademy.myhotel.api.exceptions;

import com.tinqinacademy.myhotel.api.exceptions.messages.Messages;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotFoundException extends RuntimeException{

    private String firstName;
    private String lastName;
    private String phoneNumber;

    public UserNotFoundException(String firstName, String lastName, String phoneNumber) {
        super(String.format(Messages.USER_NOT_FOUND, firstName, lastName, phoneNumber));
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

}
