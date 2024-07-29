package com.tinqinacademy.myhotel.core.converters;

import com.tinqinacademy.myhotel.api.operations.booksroomspecified.BookRoomInput;
import com.tinqinacademy.myhotel.api.operations.returnsbasicinfoforroom.BasicInfoRoomOutput;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookRoomInputToReservationConverter implements Converter<BookRoomInput, Reservation.ReservationBuilder> {
    @Override
    public Reservation.ReservationBuilder convert(BookRoomInput source) {
        log.info("Started Converter - BookRoomInput to Reservation");
        Reservation.ReservationBuilder reservation = Reservation.builder()
                .startDate(source.getStartDate())
                .endDate(source.getEndDate());

        log.info("Ended Converter - BookRoomInput to Reservation");
        return reservation;
    }
}
