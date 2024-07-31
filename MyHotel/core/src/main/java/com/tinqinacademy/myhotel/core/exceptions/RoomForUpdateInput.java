package com.tinqinacademy.myhotel.core.exceptions;

import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomForUpdateInput {
    private Integer floor;
    private String roomNo;
    private String bathroomType;
    private BigDecimal price;
    private List<Bed> beds;
}
