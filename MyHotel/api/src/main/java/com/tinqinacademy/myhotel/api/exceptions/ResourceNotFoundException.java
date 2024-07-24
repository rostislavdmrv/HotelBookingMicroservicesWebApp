package com.tinqinacademy.myhotel.api.exceptions;

import com.tinqinacademy.myhotel.api.exceptions.messages.Messages;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Setter
@Getter
public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;

    public ResourceNotFoundException(String resourceName) {
        super(String.format(Messages.RESOURCE_NOT_FOUND, resourceName));
        this.resourceName = resourceName;

    }
}
