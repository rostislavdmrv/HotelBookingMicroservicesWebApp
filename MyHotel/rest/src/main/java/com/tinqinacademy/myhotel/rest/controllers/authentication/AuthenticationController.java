package com.tinqinacademy.myhotel.rest.controllers.authentication;

import com.tinqinacademy.myhotel.api.interfaces.authentication.AuthenticationService;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpInput;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpOperation;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpOutput;
import com.tinqinacademy.myhotel.rest.controllers.BaseController;
import com.tinqinacademy.myhotel.rest.restapiroutes.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication REST APIs")
public class AuthenticationController extends BaseController {

    private final AuthenticationService authenticationService;
    private final SignUpOperation signUpOperation;

    @Operation(
            summary = "Sign Up User",
            description = "This endpoint allows a user to sign up by providing required registration details. A successful registration will return the user's ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data provided. The request may be missing required fields or contain invalid data."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. The user may not have the necessary permissions to perform this action."
            )
    })

    @PostMapping(RestApiRoutes.SIGN_UP)
    public ResponseEntity<?> signUp(@RequestBody SignUpInput input) {

        Either<ErrorWrapper,SignUpOutput> output = signUpOperation.process(input);
        return handle(output);
    }
}
