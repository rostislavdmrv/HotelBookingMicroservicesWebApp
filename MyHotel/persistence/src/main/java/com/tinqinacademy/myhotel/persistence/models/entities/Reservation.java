package com.tinqinacademy.myhotel.persistence.models.entities;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Reservation {
    private UUID id;
    private UUID roomId;
    private UUID userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
}
