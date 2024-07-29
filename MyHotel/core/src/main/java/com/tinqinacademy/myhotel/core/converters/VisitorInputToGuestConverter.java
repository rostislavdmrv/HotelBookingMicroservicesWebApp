package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.models.input.VisitorInput;
import com.tinqinacademy.myhotel.persistence.models.entities.Guest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VisitorInputToGuestConverter implements Converter<VisitorInput, Guest> {

    @Override
    public Guest convert(VisitorInput source) {
        log.info("Started Converter - VisitorInput to Guest");
        Guest guest = Guest.builder()
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .phoneNumber(source.getPhoneNo())
                .idCardNumber(source.getIdCardNo())
                .idCardValidity(source.getIdCardValidity())
                .idCardIssueAuthority(source.getIdCardIssueAuthority())
                .idCardIssueDate(source.getIdCardIssueDate())
                .birthdate(source.getBirthdate())
                .build();
        log.info("Ended Converter - VisitorInput to Guest");
        return guest;
    }
}
