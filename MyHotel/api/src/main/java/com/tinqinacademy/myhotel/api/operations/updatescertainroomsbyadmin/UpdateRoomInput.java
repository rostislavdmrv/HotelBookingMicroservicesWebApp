package com.tinqinacademy.myhotel.api.operations.updatescertainroomsbyadmin;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateRoomInput {

    
    @JsonIgnore
    private String roomId;

    @NotBlank(message = "Bed size cannot be blank")
    @Size(min = 5, max = 30, message = "Bed size cannot exceed 30 characters")
    private String bedSize;

    @NotBlank(message = "Bathroom type cannot be blank")
    @Size(min = 5, max = 30, message = "Bathroom type cannot exceed 30 characters")
    private String bathroomType;


    @NotNull(message = "Floor cannot be blank")
    @Min(value = 0, message = "Floor must be 0 (ground floor) or a positive integer")
    private Integer floor;

    @NotBlank(message = "Room number cannot be blank")
    @Size(min = 1, max = 10, message = "Room number cannot exceed 10 characters" )
    private String roomNo;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must be a numeric value with up to 10 integer digits and 2 fractional digits")
    private BigDecimal price;

}
