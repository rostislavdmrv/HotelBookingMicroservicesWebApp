package com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.myhotel.api.base.OperationInput;
import com.tinqinacademy.myhotel.api.validations.bathroomtype.BathroomTypeValidation;
import com.tinqinacademy.myhotel.api.validations.bedsize.BedSizeValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateRoomInput  implements OperationInput {

    @JsonIgnore
    @UUID
    private String roomId;

    @NotNull(message = "Bed sizes cannot be null")
    @BedSizeValidation
    private List<String> beds;

    @Schema(example = "private")
    @Size(min = 5, max = 20, message = "Bathroom type cannot exceed 20 characters")
    @BathroomTypeValidation(message = "Bathroom type cannot be blank")
    private String bathroomType;

    @Schema(example = "1")
    @NotNull(message = "Floor cannot be blank")
    @Min(value = 0, message = "Floor must be 0 (ground floor) or a positive integer")
    private Integer floor;

    @Schema(example = "18A")
    @NotBlank(message = "Room number cannot be blank")
    @Size(min = 2, max = 10, message = "Room number cannot exceed 10 characters" )
    @Pattern(regexp = "[0-9]{1,10}[A-Z]?", message = "Valid room numbers start with numbers and could end with a letter")
    private String roomNo;

    @Schema(example = "89.99")
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must be a numeric value with up to 10 integer digits and 2 fractional digits")
    private BigDecimal price;

}
