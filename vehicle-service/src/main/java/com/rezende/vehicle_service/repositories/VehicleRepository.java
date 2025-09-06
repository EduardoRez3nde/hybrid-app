package com.rezende.vehicle_service.repositories;

import com.rezende.vehicle_service.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Optional<Vehicle> findByPlate(final String plate);

    Page<Vehicle> findByDriverId(final String driverId);
}
