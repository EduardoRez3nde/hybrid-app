package com.rezende.taxi_service.repositories;

import com.rezende.taxi_service.entities.RideRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RideRatingRepository extends JpaRepository<RideRating, UUID> {
}
