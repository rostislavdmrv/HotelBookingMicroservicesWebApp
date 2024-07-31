package com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom;

import com.tinqinacademy.myhotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicInfoRoomInput implements OperationInput {

    @NotBlank(message = "Room ID cannot be blank")
    private String roomId;
}
