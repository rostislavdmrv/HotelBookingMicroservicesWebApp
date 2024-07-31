package com.tinqinacademy.myhotel.core.processors;

import com.tinqinacademy.myhotel.api.errors.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.EmailAlreadyExistsException;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpInput;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpOperation;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.User;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class SignUpOperationProcessor extends BaseOperationProcessor implements SignUpOperation {
    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;
    protected SignUpOperationProcessor(ConversionService conversionService, Validator validator, UserRepository userRepository, ErrorHandler errorHandler) {
        super(conversionService, validator);
        this.userRepository = userRepository;
        this.errorHandler = errorHandler;
    }

    @Override
    public Either<ErrorWrapper, SignUpOutput> process(SignUpInput input) {
        log.info("Start signUp: {}", input);

        return Try.of(() -> performSignUp(input))
                .toEither()
                .mapLeft(errorHandler::handleErrors);
    }
    private SignUpOutput performSignUp(SignUpInput input) {
        validateInput(input);
        User user = createUser(input);
        checkEmailUniqueness(user.getEmail());
        saveUser(user);

        SignUpOutput output = buildSignUpOutput(user);
        log.info("End signUp: {}", output);
        return output;
    }
    private User createUser(SignUpInput input) {
        return Objects.requireNonNull(conversionService.convert(input, User.UserBuilder.class))
                .birthdate(generateRandomBirthday())
                .id(UUID.randomUUID())
                .build();
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
    }

    private void saveUser(User user) {
        userRepository.save(user);
    }

    private SignUpOutput buildSignUpOutput(User user) {
        return SignUpOutput.builder()
                .id(user.getId().toString())
                .build();
    }

    private LocalDate generateRandomBirthday() {
        LocalDate currentDate = LocalDate.now();
        LocalDate latestDate = currentDate.minusYears(18);
        LocalDate earliestDate = currentDate.minusYears(100);

        long daysBetween = ChronoUnit.DAYS.between(earliestDate, latestDate);
        long randomDays = ThreadLocalRandom.current().nextLong(daysBetween + 1);

        return earliestDate.plusDays(randomDays);
    }
}
