package com.tinqinacademy.myhotel.core.services.authentication;

import com.tinqinacademy.myhotel.api.exceptions.EmailAlreadyExistsException;
import com.tinqinacademy.myhotel.api.interfaces.authentication.AuthenticationService;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpInput;
import com.tinqinacademy.myhotel.api.operations.singup.SignUpOutput;
import com.tinqinacademy.myhotel.persistence.models.entities.User;
import com.tinqinacademy.myhotel.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private  final UserRepository userRepository;
    @Override
    public SignUpOutput signUp(SignUpInput input) {
        log.info("Start signUp: {}", input);

        // Създаване на User обект чрез Builder
        User user = User.builder()
                .email(input.getEmail())
                .userPassword(input.getPassword())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .birthdate(generateRandomBirthday())
                .phoneNumber(input.getPhoneNo())
                .id(UUID.randomUUID())
                .build();


        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }


        userRepository.save(user);


        SignUpOutput output = SignUpOutput.builder()
                .id(user.getId())
                .build();

        log.info("End signUp: {}", output);
        return output;
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
