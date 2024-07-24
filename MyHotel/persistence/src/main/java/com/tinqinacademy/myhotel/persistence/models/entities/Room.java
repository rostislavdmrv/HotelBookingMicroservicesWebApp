package com.tinqinacademy.myhotel.persistence.models.entities;

import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "room_floor", nullable = false)
    private Integer roomFloor;

    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "bathroom_type", nullable = false)
    private BathroomType bathroomType;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToMany
    @JoinTable(
            name = "room_bed",
            joinColumns = @JoinColumn(name = "room_id" ,referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "bed_id",referencedColumnName = "id")
    )
    private List<Bed> beds;
}
