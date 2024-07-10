package com.tinqinacademy.myhotel.models.operations.retrivessroomrenteroccupancies;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRenterOutput {
    private LocalDate startDate;
    private LocalDate endDate;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String idCardNo;
    private LocalDate idCardValidity;
    private String idCardIssueAuthority;
    private LocalDate idCardIssueDate;
}
