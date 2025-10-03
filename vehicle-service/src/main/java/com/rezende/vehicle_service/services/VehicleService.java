package com.rezende.vehicle_service.services;

import com.rezende.vehicle_service.dto.CreateVehicleRequestDTO;
import com.rezende.vehicle_service.dto.VehicleResponseDTO;
import com.rezende.vehicle_service.entity.Vehicle;
import com.rezende.vehicle_service.enums.VehicleStatus;
import com.rezende.vehicle_service.events.VehicleApprovedEvent;
import com.rezende.vehicle_service.exceptions.PlateAlreadyExistsException;
import com.rezende.vehicle_service.exceptions.VehicleNotFoundException;
import com.rezende.vehicle_service.repositories.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleEventProducer vehicleEventProducer;

    public VehicleService(
            final VehicleRepository vehicleRepository,
            final VehicleEventProducer vehicleEventProducer
    ) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleEventProducer = vehicleEventProducer;
    }

    @Transactional
    public VehicleResponseDTO register(final String driverId, final CreateVehicleRequestDTO dto) {

        vehicleRepository.findByPlate(dto.plate())
                .ifPresent(vehicle -> {
                    throw new PlateAlreadyExistsException("A vehicle with this plate is already registered.");
                });

        final Vehicle vehicle = Vehicle.builder()
                .driverId(UUID.fromString(driverId))
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
    public List<VehicleResponseDTO> getVehiclesByDriver(final String driverId) {
        final List<Vehicle> vehicles = vehicleRepository.findByDriverId(UUID.fromString(driverId));
        return vehicles.stream().map(VehicleResponseDTO::of).toList();
    }

    @Transactional
    public VehicleResponseDTO approvedVehicle(final String vehicleId) {

        final Vehicle vehicle = vehicleRepository.findById(UUID.fromString(vehicleId))
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with id %d not found", vehicleId));

        vehicle.setStatus(VehicleStatus.APPROVED);
        final Vehicle vehicleSave = vehicleRepository.save(vehicle);

        vehicleEventProducer.sendVehicleApprovedEvent(VehicleApprovedEvent.of(vehicleSave));

        return VehicleResponseDTO.of(vehicleSave);
    }
}
