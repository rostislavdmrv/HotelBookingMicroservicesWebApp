package com.tinqinacademy.myhotel.api.operations.updatespartialroomsbyadmin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.myhotel.api.base.OperationInput;
import com.tinqinacademy.myhotel.api.validations.bathroomtype.BathroomTypeValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PartialUpdateRoomInput implements OperationInput {

    @JsonIgnore
    private String roomId;

    private List<String> beds;

    @Schema(example = "private")
    @Size(min = 5, max = 30, message = "Bathroom type cannot exceed 30 characters")
    private String bathroomType;

    @Schema(example = "1")
    @Min(value = 0, message = "Floor must be 0 (ground floor) or a positive integer")
    private Integer roomFloor;

    @Schema(example = "18A")
    @Size(min = 1, max = 10, message = "Room number cannot exceed 10 characters" )
    private String roomNumber;

    @Schema(example = "89.99")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must be a numeric value with up to 10 integer digits and 2 fractional digits")
    private BigDecimal price;
}
