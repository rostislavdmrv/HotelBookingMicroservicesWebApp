package com.tinqinacademy.myhotel.core.processors.system;

import com.tinqinacademy.myhotel.api.errors.ErrorHandler;
import com.tinqinacademy.myhotel.api.exceptions.NoMatchException;
import com.tinqinacademy.myhotel.api.exceptions.ResourceNotFoundException;
import com.tinqinacademy.myhotel.api.exceptions.messages.Messages;
import com.tinqinacademy.myhotel.api.models.error.ErrorWrapper;
import com.tinqinacademy.myhotel.api.models.output.VisitorReportOutput;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportInput;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportOperation;
import com.tinqinacademy.myhotel.api.operations.retrivesreports.ReportOutput;
import com.tinqinacademy.myhotel.core.processors.base.BaseOperationProcessor;
import com.tinqinacademy.myhotel.persistence.models.entities.Guest;
import com.tinqinacademy.myhotel.persistence.models.entities.Reservation;
import com.tinqinacademy.myhotel.persistence.models.entities.Room;
import com.tinqinacademy.myhotel.persistence.repositories.GuestRepository;
import com.tinqinacademy.myhotel.persistence.repositories.ReservationRepository;
import com.tinqinacademy.myhotel.persistence.repositories.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ReportOperationProcessor extends BaseOperationProcessor implements ReportOperation {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final ErrorHandler errorHandler;

    protected ReportOperationProcessor(ConversionService conversionService, Validator validator, ReservationRepository reservationRepository, GuestRepository guestRepository, RoomRepository roomRepository, ErrorHandler errorHandler) {
        super(conversionService, validator);
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.errorHandler = errorHandler;
    }


    @Override
    public Either<ErrorWrapper, ReportOutput> process(ReportInput input) {
        log.info("Starts getting room renters occupancy {}", input);

        return Try.of(() -> {
                    validateInput(input);
                    Map<UUID, VisitorReportOutput> guestMap = new HashMap<>();

                    if (input.getStartDate() != null && input.getEndDate() != null) {
                        processReservationsByDateRange(input, guestMap);
                    }

                    if (hasCompleteGuestDetails(input)) {
                        processGuestsByDetails(input, guestMap);
                    }

                    if (input.getRoomNo() != null) {
                        processReservationsByRoomNumber(input, guestMap);
                    }

                    if (guestMap.isEmpty()) {
                        throw new NoMatchException(Messages.NO_MATCHING);
                    }

                    List<VisitorReportOutput> values = new ArrayList<>(guestMap.values());
                    ReportOutput output = ReportOutput.builder()
                            .reports(values)
                            .build();

                    log.info("End : Successfully made report {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(errorHandler::handleErrors);

    }

    private boolean hasCompleteGuestDetails(ReportInput input) {
        return input.getFirstName() != null &&
                input.getLastName() != null &&
                input.getRoomNo() != null &&
                input.getIdCardNo() != null &&
                input.getIdCardValidity() != null &&
                input.getIdCardIssueAuthority() != null &&
                input.getIdCardIssueDate() != null;
    }

    private void processReservationsByDateRange(ReportInput input, Map<UUID, VisitorReportOutput> guestMap) {
        List<Reservation> reservations = reservationRepository
                .findByDateRange(input.getStartDate(), input.getEndDate())
                .orElse(Collections.emptyList());

        for (Reservation booking : reservations) {
            for (Guest guest : booking.getGuests()) {
                VisitorReportOutput guestOutput =
                        Objects.requireNonNull(conversionService.convert(guest, VisitorReportOutput.VisitorReportOutputBuilder.class))
                                .startDate(booking.getStartDate())
                                .endDate(booking.getEndDate())
                                .build();
                guestMap.putIfAbsent(guest.getId(), guestOutput);
            }
        }
    }

    private void processGuestsByDetails(ReportInput input, Map<UUID, VisitorReportOutput> guestMap) {
        List<Guest> matchingGuests = guestRepository.findMatchingGuests(
                input.getFirstName(),
                input.getLastName(),
                input.getPhoneNo(),
                input.getIdCardNo(),
                input.getIdCardValidity().toString(),
                input.getIdCardIssueAuthority(),
                input.getIdCardIssueDate().toString()
        ).orElse(Collections.emptyList());

        for (Guest guest : matchingGuests) {
            List<Reservation> reservations = reservationRepository.findByGuestIdCardNumber(guest.getIdCardNumber())
                    .orElse(Collections.emptyList());

            if (reservations.isEmpty()) {
                if (!guestMap.containsKey(guest.getId())) {
                    VisitorReportOutput guestOutput = conversionService.convert(guest, VisitorReportOutput.class);
                    guestMap.put(guest.getId(), guestOutput);
                }
            } else {
                for (Reservation booking : reservations) {
                    if (!guestMap.containsKey(guest.getId())) {
                        VisitorReportOutput guestOutput =
                                Objects.requireNonNull(conversionService.convert(guest, VisitorReportOutput.VisitorReportOutputBuilder.class))
                                        .startDate(booking.getStartDate())
                                        .endDate(booking.getEndDate())
                                        .build();
                        guestMap.put(guest.getId(), guestOutput);
                    }
                }
            }
        }
    }

    private void processReservationsByRoomNumber(ReportInput input, Map<UUID, VisitorReportOutput> guestMap) {
        Room room = roomRepository.findByRoomNumber(input.getRoomNo())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomNo", input.getRoomNo()));

        List<Reservation> reservations = reservationRepository.findByRoomId(room.getId())
                .orElse(Collections.emptyList());

        for (Reservation booking : reservations) {
            for (Guest guest : booking.getGuests()) {
                if (!guestMap.containsKey(guest.getId())) {
                    VisitorReportOutput guestOutput =
                            Objects.requireNonNull(conversionService.convert(guest, VisitorReportOutput.VisitorReportOutputBuilder.class))
                                    .startDate(booking.getStartDate())
                                    .endDate(booking.getEndDate())
                                    .build();
                    guestMap.put(guest.getId(), guestOutput);
                }
            }
        }

    }
}
