package com.tinqinacademy.myhotel.api.operations.singup;

import com.tinqinacademy.myhotel.api.base.OperationInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpInput implements OperationInput {

    @Schema(example = "Elena")
    @NotBlank(message = "Field firstName must not be empty")
    @Size(min = 2, max = 20, message = "Field firstName must be between 2-20 characters")
    private String firstName;

    @Schema(example = "Hristova")
    @NotBlank(message = "Field lastName must not be empty")
    @Size(min = 2, max = 20, message = "Field lastName must be between 2-20 characters")
    private String lastName;

    @Schema(example = "elena.2001@gmail.com")
    @Email(message = "Field email must be valid")
    @NotNull(message = "Field email cannot be null")
    private String email;


    @Schema(example = "password")
    @NotBlank(message = "Field password cannot be blank")
    @Size(min = 8, max = 255, message = "Field password must be 8-255 chars")
    private String password;


    @Schema(example = "password")
    @NotBlank(message = "Field password cannot be blank")
    private String confirmPassword;


    @Schema(example = "+359896325812")
    @NotBlank(message = "Field phoneNo must not be empty")
    @Pattern(regexp = "^\\+[1-9]{1}[0-9]{3,14}$")
    private String phoneNo;
}
