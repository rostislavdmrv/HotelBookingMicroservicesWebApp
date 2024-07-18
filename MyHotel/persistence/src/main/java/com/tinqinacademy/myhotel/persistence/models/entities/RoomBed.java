package com.tinqinacademy.myhotel.persistence.models.entities;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoomBed {
    private UUID bedId;
    private UUID roomId;
}
