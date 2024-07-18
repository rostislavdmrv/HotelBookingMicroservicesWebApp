package com.tinqinacademy.myhotel.persistence.models.entities;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Bed {
    private UUID id;
    private Bed bedSize;
    private Integer capacity;
}
