package com.tinqinacademy.myhotel.persistence.models.entities;

import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import com.tinqinacademy.myhotel.persistence.repositories.Entity;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Room implements Entity {
    private UUID id;
    private Integer roomFloor;
    private String roomNumber;
    private BathroomType bathroomType;
    private BigDecimal price;

    private List<Bed> beds;
}
