package com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin;

import com.tinqinacademy.myhotel.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialUpdateRoomOutput implements OperationOutput {
    private String roomId;
}
