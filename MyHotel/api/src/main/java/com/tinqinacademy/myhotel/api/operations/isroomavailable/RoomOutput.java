package com.tinqinacademy.myhotel.api.operations.isroomavailable;

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
