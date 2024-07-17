package com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateRoomOutput {
    private String roomId;
}
