package com.tinqinacademy.myhotel.persistence.models.entities;

import com.tinqinacademy.myhotel.persistence.models.enums.BedSize;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_size" , nullable = false, length = 20)
    private BedSize bedSize;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToMany(mappedBy = "beds")
    private Set<Room> rooms;
}
