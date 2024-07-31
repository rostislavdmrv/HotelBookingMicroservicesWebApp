package com.tinqinacademy.myhotel.api.operations.singup;

import com.tinqinacademy.myhotel.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpOutput implements OperationOutput {
    private String id;
}
