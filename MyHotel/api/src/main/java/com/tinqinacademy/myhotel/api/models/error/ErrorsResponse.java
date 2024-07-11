package com.tinqinacademy.myhotel.api.models.error;

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
