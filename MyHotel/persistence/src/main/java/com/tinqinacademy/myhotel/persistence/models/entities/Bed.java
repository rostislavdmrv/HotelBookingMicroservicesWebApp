package com.tinqinacademy.myhotel.persistence.models.entities;

import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "beds")
public class Bed   {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_size" , nullable = false, length = 20)
    private BedSize bedSize;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
