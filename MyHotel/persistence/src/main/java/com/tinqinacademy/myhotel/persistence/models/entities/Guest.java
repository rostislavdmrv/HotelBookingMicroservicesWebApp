package com.tinqinacademy.myhotel.persistence.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @Column(name = "id_card_number", unique = true, length = 20, nullable = false)
    private String idCardNumber;

    @Column(name = "id_card_issue_authority", length = 20 ,nullable = false)
    private String idCardIssueAuthority;

    @Column(name = "id_card_issue_date",nullable = false)
    private LocalDate idCardIssueDate;

    @Column(name = "id_card_validity", nullable = false)
    private LocalDate idCardValidity;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
