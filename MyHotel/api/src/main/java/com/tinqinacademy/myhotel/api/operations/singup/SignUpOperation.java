package com.tinqinacademy.myhotel.api.operations.singup;

import com.tinqinacademy.myhotel.api.base.OperationProcessor;
import org.springframework.stereotype.Service;

@Service
public interface SignUpOperation extends OperationProcessor<SignUpInput, SignUpOutput> {
}
