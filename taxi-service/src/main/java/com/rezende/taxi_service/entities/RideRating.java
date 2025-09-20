package com.rezende.taxi_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "tb_ride_rating")
public class RideRating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID rideId;

    @Column(nullable = false)
    private UUID driverId;

    @Column(nullable = false)
    private UUID passengerId;

    @Column(nullable = false)
    @Basic
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    private Instant createdAt;

    @PrePersist
    @PreUpdate
    private void checkRatingConstraint() {
        if (rating < 1 || rating > 5) {
            throw new IllegalStateException("A avaliação deve estar entre 1 e 5.");
        }
    }
}
