package com.tinqinacademy.myhotel.exceptions;

import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorsResponse {
    private String field;
    private String message;



}
