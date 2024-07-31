package com.tinqinacademy.myhotel.api.operations.removesroomreservation;

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
public class UnbookRoomInput implements OperationInput {
    @NotBlank(message = "Booking ID cannot be blank")
    private String bookingId;
}
