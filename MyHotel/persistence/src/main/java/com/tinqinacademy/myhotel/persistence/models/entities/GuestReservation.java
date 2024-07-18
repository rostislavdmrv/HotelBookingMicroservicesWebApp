package com.tinqinacademy.myhotel.persistence.models.entities;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GuestReservation {
    private UUID reservationId;
    private UUID guestId;
}
