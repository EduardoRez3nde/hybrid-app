package com.rezende.vehicle_service.services;

import com.rezende.vehicle_service.dto.CreateVehicleRequestDTO;
import com.rezende.vehicle_service.dto.VehicleResponseDTO;
import com.rezende.vehicle_service.entity.Vehicle;
import com.rezende.vehicle_service.enums.VehicleStatus;
import com.rezende.vehicle_service.enums.VehicleType;
import com.rezende.vehicle_service.exceptions.PlateAlreadyExistsException;
import com.rezende.vehicle_service.repositories.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public VehicleResponseDTO register(final String driverId, final CreateVehicleRequestDTO dto) {

        vehicleRepository.findByPlate(dto.plate())
                .ifPresent(vehicle -> {
                    throw new PlateAlreadyExistsException("A vehicle with this plate is already registered.");
                });

        final Vehicle vehicle = Vehicle.builder()
                .driverId(driverId)
                .plate(dto.plate())
                .make(dto.make())
                .model(dto.model())
                .color(dto.color())
                .year(dto.year())
                .type(dto.type())
                .status(VehicleStatus.PENDING_APPROVAL)
                .build();

        final Vehicle vehicleSave = vehicleRepository.save(vehicle);

        return VehicleResponseDTO.of(vehicleSave);
    }

    @Transactional(readOnly = true)
    public Page<VehicleResponseDTO> getVehiclesByDriver(final String driverId) {
        final Page<Vehicle> vehicles = vehicleRepository.findByDriverId(driverId);
        return vehicles.map(VehicleResponseDTO::of);
    }
}
