package com.tinqinacademy.myhotel.persistence.models.entities;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String userPassword;
    private String email;
    private LocalDate birthday;
    private String phoneNumber;
}
