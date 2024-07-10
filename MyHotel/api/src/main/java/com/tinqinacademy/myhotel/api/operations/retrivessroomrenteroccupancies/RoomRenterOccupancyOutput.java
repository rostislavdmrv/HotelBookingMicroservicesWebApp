package com.tinqinacademy.myhotel.models.operations.retrivessroomrenteroccupancies;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRenterOccupancyOutput {
    private List<RoomRenterOutput> roomRenters;
}
