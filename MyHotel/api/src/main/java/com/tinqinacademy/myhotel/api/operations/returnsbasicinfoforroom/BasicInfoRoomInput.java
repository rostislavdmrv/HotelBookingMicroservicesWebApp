package com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicInfoRoomInput {

    @NotBlank(message = "Room ID cannot be blank")
    private String roomId;
}
