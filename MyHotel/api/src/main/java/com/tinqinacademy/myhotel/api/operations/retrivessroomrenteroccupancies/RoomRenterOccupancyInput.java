package com.tinqinacademy.myhotel.api.operations.retrivessroomrenteroccupancies;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRenterOccupancyInput {

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2,max = 30, message = "First name cannot exceed 30 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min =2,max = 30, message = "Last name cannot exceed 30 characters")
    private String lastName;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^\\+\\d{1,3}\\d{4,14}$",
            message = "Phone number must start with a '+' followed by the country code and subscriber number, totaling 5 to 17 digits"
    )
    private String phoneNo;

    @NotBlank(message = "ID card number cannot be blank")
    @Size(min= 5, max = 15, message = "ID card number cannot exceed 15 characters")
    private String idCardNo;

    @NotNull(message = "ID card validity cannot be null")
    @FutureOrPresent(message = "ID card validity must be today or in the future")
    private LocalDate idCardValidity;

    @NotBlank(message = "ID card issue authority cannot be blank")
    @Size(min =5 ,max = 30, message = "ID card issue authority cannot exceed 30 characters")
    private String idCardIssueAuthority;

    @NotNull(message = "ID card issue date cannot be null")
    @PastOrPresent(message = "ID card issue date must be in the past or present")
    private LocalDate idCardIssueDate;

    @NotBlank(message = "Room number cannot be blank")
    @Size(min = 1, max = 10, message = "Room number cannot exceed 10 characters" )
    private Integer roomNo;
}
