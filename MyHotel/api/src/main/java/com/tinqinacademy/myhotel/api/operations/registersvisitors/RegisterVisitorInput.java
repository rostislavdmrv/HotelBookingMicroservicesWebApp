package com.tinqinacademy.myhotel.models.operations.registersvisitors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterVisitorInput {

    @Valid
    @NotEmpty
    private List<VisitorInput> visitorInputs;
}
