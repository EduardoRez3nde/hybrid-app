package com.rezende.taxi_service.repositories;

import com.rezende.taxi_service.entities.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {  }
