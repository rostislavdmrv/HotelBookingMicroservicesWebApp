package com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin;

import com.tinqinacademy.myhotel.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateRoomOutput implements OperationOutput {
    private String roomId;
}
