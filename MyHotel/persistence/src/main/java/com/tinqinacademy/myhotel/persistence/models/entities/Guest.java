package com.tinqinacademy.myhotel.persistence.models.entities;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Guest {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String idCardNumber;
    private String idIssueAuthority;
    private LocalDate idIssueDate;
    private LocalDate idValidity;
}
