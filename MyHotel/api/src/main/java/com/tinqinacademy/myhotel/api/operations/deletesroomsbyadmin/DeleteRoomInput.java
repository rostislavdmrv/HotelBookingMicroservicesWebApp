package com.tinqinacademy.myhotel.api.operations.deletesroomsbyadmin;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteRoomInput {
    @NotBlank(message = "Room ID cannot be blank")
    private String roomId;
}
