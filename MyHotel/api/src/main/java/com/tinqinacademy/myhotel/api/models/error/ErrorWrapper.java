package com.tinqinacademy.myhotel.api.models.error;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorWrapper {

    private List<ErrorsResponse> errors;
    private HttpStatus errorHttpCode;

}
