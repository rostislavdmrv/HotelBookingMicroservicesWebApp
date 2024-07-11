package com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicInfoRoomOutput {

    @JsonIgnore
    private String roomId;
    private BigDecimal price;
    private  Integer  floor;
    private  String bedSize;
    private String bathroomType;
    private Integer bedCount;
    public LocalDate datesOccupied;
}
