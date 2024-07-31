package com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin;

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
public class DeleteRoomInput implements OperationInput {
    @NotBlank(message = "Room ID cannot be blank")
    private String roomId;
}
