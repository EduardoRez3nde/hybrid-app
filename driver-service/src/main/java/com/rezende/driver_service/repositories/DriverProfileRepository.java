package com.rezende.driver_service.repositories;

import com.rezende.driver_service.entities.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, UUID> {
}
