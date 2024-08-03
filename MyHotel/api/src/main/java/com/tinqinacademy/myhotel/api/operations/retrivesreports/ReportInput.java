package com.tinqinacademy.myhotel.api.operations.retrivesreports;

import com.tinqinacademy.myhotel.api.base.OperationInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportInput implements OperationInput {

    @Schema(example = "2024-08-01")
    private LocalDate startDate;

    @Schema(example = "2025-08-01")
    private LocalDate endDate;

    @Schema(example = "Rostislav")
    @Size(min = 2,max = 30, message = "First name cannot exceed 30 characters")
    private String firstName;

    @Schema(example = "Demirov")
    @Size(min =2,max = 30, message = "Last name cannot exceed 30 characters")
    private String lastName;

    @Schema(example = "+359898545312")
    @Pattern(
            regexp = "^\\+[1-9]{1}[0-9]{3,14}$",
            message = "Phone number must start with a '+' followed by the country code and subscriber number digits"
    )
    private String phoneNo;

    @Schema(defaultValue = "55555555")
    @Size(min= 5, max = 15, message = "ID card number cannot exceed 15 characters")
    private String idCardNo;

    @Schema(example = "2028-10-10")
    @FutureOrPresent(message = "ID card validity must be today or in the future")
    private LocalDate idCardValidity;

    @Schema(example = "MVR VARNA")
    @Size(min =5 ,max = 30, message = "ID card issue authority cannot exceed 30 characters")
    private String idCardIssueAuthority;

    @Schema(example = "2020-10-10")
    @PastOrPresent(message = "ID card issue date must be in the past or present")
    private LocalDate idCardIssueDate;

    @Schema(example = "11C")
    @Size(min = 1, max = 10, message = "Room number cannot exceed 10 characters" )
    private String roomNo;
}
