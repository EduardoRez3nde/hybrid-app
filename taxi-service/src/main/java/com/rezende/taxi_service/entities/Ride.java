package com.rezende.taxi_service.entities;

import com.rezende.taxi_service.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@Entity
@Table(name = "tb_rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String passengerId;

    private String driverId;

    private UUID vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "coordinates", column = @Column(name = "origin_coordinates", nullable = false))})
    private Location origin;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "coordinates", column = @Column(name = "destination_coordinates", nullable = false))})
    private Location destination;

    private BigDecimal estimatedPrice;

    private BigDecimal finalPrice;

    private Instant acceptedAt;

    private Instant startedAt;

    private Instant completedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;


}