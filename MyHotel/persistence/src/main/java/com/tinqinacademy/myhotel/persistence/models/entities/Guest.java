package com.tinqinacademy.myhotel.persistence.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "id_card_number", unique = true, length = 20, nullable = false)
    private String idCardNumber;

    @Column(name = "id_issue_authority", length = 100)
    private String idIssueAuthority;

    @Column(name = "id_issue_date",nullable = false)
    private LocalDate idIssueDate;

    @Column(name = "id_validity", nullable = false)
    private LocalDate idValidity;

    @ManyToMany
    @JoinTable(
            name = "guest_reservation",
            joinColumns = @JoinColumn(name = "guest_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id",referencedColumnName = "id")
    )
    private Set<Reservation> reservations;
}
