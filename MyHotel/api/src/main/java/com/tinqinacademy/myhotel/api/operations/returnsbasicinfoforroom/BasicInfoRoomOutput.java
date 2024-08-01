package com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.myhotel.api.base.OperationOutput;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BasicInfoRoomOutput implements OperationOutput {

    @JsonIgnore
    private String roomId;
    private BigDecimal price;
    private Integer floor;
    private List<String> bedSize;
    private String bathroomType;
    private Integer bedCount;
    public List<LocalDate> datesOccupied;
}
