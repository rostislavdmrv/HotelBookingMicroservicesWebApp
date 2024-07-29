package com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.myhotel.api.models.enums.BathroomType;
import com.tinqinacademy.myhotel.api.models.enums.BedSize;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BasicInfoRoomOutput {

    @JsonIgnore
    private String roomId;
    private BigDecimal price;
    private Integer floor;
    private List<String> bedSize;
    private String bathroomType;
    private Integer bedCount;
    public List<LocalDate> datesOccupied;
}
