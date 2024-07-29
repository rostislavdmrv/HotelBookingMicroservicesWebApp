package com.tinqinacademy.myhotel.api.operations.isroomavailable;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomOutput {

    private List<String> ids;


}
