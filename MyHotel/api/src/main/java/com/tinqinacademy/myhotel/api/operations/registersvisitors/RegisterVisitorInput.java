package com.tinqinacademy.myhotel.api.operations.registersvisitors;

import com.tinqinacademy.myhotel.api.base.OperationInput;
import com.tinqinacademy.myhotel.api.models.input.VisitorInput;
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
public class RegisterVisitorInput  implements OperationInput {

    @NotEmpty(message = "The list of visitors must be at least one!")
    private List<@Valid VisitorInput> visitorInputs;
}
