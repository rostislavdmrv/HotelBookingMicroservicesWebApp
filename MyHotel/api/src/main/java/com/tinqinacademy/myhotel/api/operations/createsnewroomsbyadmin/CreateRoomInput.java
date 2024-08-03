package com.tinqinacademy.myhotel.api.operations.createsnewroomsbyadmin;

import com.tinqinacademy.myhotel.api.base.OperationInput;
import com.tinqinacademy.myhotel.api.validations.bathroomtype.BathroomTypeValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomInput implements OperationInput {

    private List<String> beds;

    @Schema(example = "private")
    @Size(min = 5, max = 20, message = "Bathroom type cannot exceed 20 characters")
    @BathroomTypeValidation(message = "Bathroom type cannot be blank")
    private String bathroomType;


    @Schema(example = "1")
    @NotNull(message = "Floor cannot be blank")
    @Min(value = 0, message = "Floor must be 0 (ground floor) or a positive integer")
    private Integer floor;

    @Schema(example = "12A")
    @NotBlank(message = "Room number cannot be blank")
    @Size(min = 1, max = 10, message = "Room number cannot exceed 10 characters" )
    private String roomNo;

    @Schema(example = "89.99")
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must be a numeric value with up to 10 integer digits and 2 fractional digits")
    private BigDecimal price;

}
