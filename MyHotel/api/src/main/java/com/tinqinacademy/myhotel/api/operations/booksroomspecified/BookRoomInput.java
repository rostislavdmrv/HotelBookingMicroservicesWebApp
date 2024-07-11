package com.tinqinacademy.myhotel.api.operations.booksroomspecified;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookRoomInput {

    @NotBlank(message = "Room ID cannot be blank")
    @JsonIgnore
    private String roomId;

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
}
