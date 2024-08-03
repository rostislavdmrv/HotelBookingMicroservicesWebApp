package com.tinqinacademy.myhotel.api.operations.isroomavailable;

import com.tinqinacademy.myhotel.api.base.OperationInput;
import com.tinqinacademy.myhotel.api.validations.bathroomtype.BathroomTypeValidation;
import com.tinqinacademy.myhotel.api.validations.bedsize.BedSizeValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInput implements OperationInput {

    @NotBlank(message = "ID cannot be blank")
    private String id;

    @Schema(example = "2024-08-01")
    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @Schema(example = "2024-08-01")
    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be  in the future")
    private LocalDate endDate;

    @Schema(example = "2")
    @NotNull(message = "Bed count cannot be null")
    @Positive(message = "Bed count must be positive")
    private Integer bedCount;

    @Schema(example = "private")
    @Size(min= 3,max = 10, message = "Bathroom type cannot exceed 10 characters")
    @BathroomTypeValidation(message = "Bathroom type cannot be blank")
    private String bathroomType;

    @Schema(example = "kingSized")
    @Size(min =3, max = 10, message = "Bed size cannot exceed 10 characters")
    @BedSizeValidation(message = "Bed size cannot be blank")
    private String bedSize;

}
