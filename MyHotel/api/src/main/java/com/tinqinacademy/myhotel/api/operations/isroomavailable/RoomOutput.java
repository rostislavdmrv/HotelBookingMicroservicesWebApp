package com.tinqinacademy.myhotel.models.operations.isroomavailable;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomOutput {

    private List<String> ids;


}
