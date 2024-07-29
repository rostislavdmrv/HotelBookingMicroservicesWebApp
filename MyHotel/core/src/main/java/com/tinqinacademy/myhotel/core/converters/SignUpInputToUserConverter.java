package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.operations.singup.SignUpInput;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SignUpInputToUserConverter implements Converter<SignUpInput, User.UserBuilder> {
    @Override
    public User.UserBuilder convert(SignUpInput source) {
        log.info("Started Converter - SignUpInput to User");
        User.UserBuilder reservation = User.builder()
                .email(source.getEmail())
                .userPassword(source.getPassword())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .phoneNumber(source.getPhoneNo());

        log.info("Ended Converter - SignUpInput to User");
        return reservation;
    }
}
