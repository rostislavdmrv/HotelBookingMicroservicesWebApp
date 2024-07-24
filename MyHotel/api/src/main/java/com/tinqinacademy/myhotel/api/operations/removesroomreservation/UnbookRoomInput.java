package com.tinqinacademy.myhotel.api.operations.removesroomreservation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnbookRoomInput {
    @NotBlank(message = "Room ID cannot be blank")
    private UUID roomId;
}
