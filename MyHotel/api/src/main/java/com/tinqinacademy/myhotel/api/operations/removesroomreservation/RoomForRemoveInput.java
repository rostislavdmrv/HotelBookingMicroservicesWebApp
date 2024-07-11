package com.tinqinacademy.myhotel.api.operations.removesroomreservation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomForRemoveInput {
    @NotBlank(message = "Room ID cannot be blank")
    private String roomId;
}
