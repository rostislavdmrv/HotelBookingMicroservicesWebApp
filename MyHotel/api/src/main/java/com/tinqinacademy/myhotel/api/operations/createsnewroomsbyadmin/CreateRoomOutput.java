package com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomOutput {
    private UUID roomId;
}
