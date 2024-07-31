package com.tinqinacademy.myhotel.api.operations.isroomavailable;

import com.tinqinacademy.myhotel.api.base.OperationOutput;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomOutput implements OperationOutput {

    private List<String> ids;


}
