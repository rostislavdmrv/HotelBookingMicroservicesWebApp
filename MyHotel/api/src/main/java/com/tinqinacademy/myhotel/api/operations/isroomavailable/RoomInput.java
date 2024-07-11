package com.tinqinacademy.myhotel.api.operations.isroomavailable;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInput {

    @NotBlank(message = "ID cannot be blank")
    private String id;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    @NotNull(message = "Bed count cannot be null")
    @Positive(message = "Bed count must be positive")
    private Integer bedCount;

    @NotBlank(message = "Bathroom type cannot be blank")
    @Size(min= 5,max = 30, message = "Bathroom type cannot exceed 30 characters")
    private String bathroomType;

    @NotBlank(message = "Bed size cannot be blank")
    @Size(min =5, max = 30, message = "Bed size cannot exceed 30 characters")
    private String bedSize;



}
