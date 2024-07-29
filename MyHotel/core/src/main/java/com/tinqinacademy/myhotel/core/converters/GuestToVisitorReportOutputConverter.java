package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.models.output.VisitorReportOutput;
import com.tinqinacademy.myhotel.persistence.models.entities.Guest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GuestToVisitorReportOutputConverter implements Converter<Guest, VisitorReportOutput.VisitorReportOutputBuilder> {
    @Override
    public VisitorReportOutput.VisitorReportOutputBuilder convert(Guest source) {
        log.info("Started Converter - Guest to VisitorReportOutput");

        VisitorReportOutput.VisitorReportOutputBuilder guest = VisitorReportOutput.builder()
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .phoneNo(source.getPhoneNumber())
                .idCardNo(source.getIdCardNumber())
                .idCardValidity(source.getIdCardValidity())
                .idCardIssueAuthority(source.getIdCardIssueAuthority())
                .idCardIssueDate(source.getIdCardIssueDate());

        log.info("Ended Converter - Guest to VisitorReportOutput");
        return guest;
    }
}
