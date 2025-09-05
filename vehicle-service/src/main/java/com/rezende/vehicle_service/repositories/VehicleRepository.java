package com.rezende.vehicle_service.repositories;

import com.rezende.vehicle_service.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Optional<Vehicle> findByPlate(final String plate);

    List<Vehicle> findByDriverId(final String driverId);
}
