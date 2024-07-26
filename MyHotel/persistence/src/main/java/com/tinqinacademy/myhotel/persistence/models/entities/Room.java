package com.tinqinacademy.myhotel.persistence.models.entities;

import com.tinqinacademy.myhotel.persistence.models.enums.BathroomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "room_floor", nullable = false)
    private Integer roomFloor;

    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "bathroom_type", nullable = false)
    private BathroomType bathroomType;

    @Column(name = "price", nullable = false,precision = 10, scale = 2)
    private BigDecimal price;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "room_bed")
    private List<Bed> beds;
}
