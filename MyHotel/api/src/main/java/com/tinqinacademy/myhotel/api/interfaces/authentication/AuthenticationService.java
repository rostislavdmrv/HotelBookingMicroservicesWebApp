package com.tinqinacademy.myhotel.api.interfaces.authentication;

import com.tinqinacademy.myhotel.api.operations.singup.SignUpInput;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpOutput;

public interface AuthenticationService {
    SignUpOutput signUp(SignUpInput signUpInput);
}
