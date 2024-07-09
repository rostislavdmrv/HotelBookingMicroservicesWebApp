package com.tinqinacademy.myhotel.exceptions;

import ch.qos.logback.core.status.ErrorStatus;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorWrapper {

    private List<ErrorsResponse> errors;
    private HttpStatus errorCode;

}
