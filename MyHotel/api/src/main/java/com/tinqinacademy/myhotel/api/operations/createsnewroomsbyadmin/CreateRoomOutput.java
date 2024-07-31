package com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin;

import com.tinqinacademy.myhotel.api.base.OperationOutput;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomOutput implements OperationOutput {
    private String roomId;
}
