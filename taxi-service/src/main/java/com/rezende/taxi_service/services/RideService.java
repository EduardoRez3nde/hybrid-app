package com.rezende.taxi_service.services;

import com.rezende.taxi_service.dto.DriverAssignedToRideEventDTO;
import com.rezende.taxi_service.dto.RequestRideDTO;
import com.rezende.taxi_service.dto.RideCreationResponseDTO;
import com.rezende.taxi_service.entities.Location;
import com.rezende.taxi_service.entities.Ride;
import com.rezende.taxi_service.enums.RideStatus;
import com.rezende.taxi_service.event.RideAcceptedByDriverEvent;
import com.rezende.taxi_service.event.RideRejectedByDriverEvent;
import com.rezende.taxi_service.event.RideRequestedEvent;
import com.rezende.taxi_service.exceptions.BusinessRuleException;
import com.rezende.taxi_service.exceptions.RideNotFoundException;
import com.rezende.taxi_service.repositories.RideRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RideEventProducer rideEventProducer;

    public RideService(
            final RideRepository rideRepository,
            final RideEventProducer rideEventProducer
    ) {
        this.rideRepository = rideRepository;
        this.rideEventProducer = rideEventProducer;
    }

    @Transactional
    public RideCreationResponseDTO requestRide(final String passengerId, final RequestRideDTO dto) {

        log.info("Recebido pedido de corrida do passageiro: {}", passengerId);

        final Ride newRide = Ride.builder()
                .passengerId(UUID.fromString(passengerId))
                .origin(Location.from(dto.origin().latitude(), dto.origin().longitude()))
                .destination(Location.from(dto.destination().latitude(), dto.destination().longitude()))
                .status(RideStatus.REQUESTED)
                .build();

        final Ride savedRide = rideRepository.save(newRide);
        log.info("Corrida criada com ID: {} e status REQUESTED.", savedRide.getId());

        final RideRequestedEvent event = new RideRequestedEvent(
                savedRide.getId(),
                savedRide.getPassengerId(),
                savedRide.getOrigin(),
                savedRide.getDestination(),
                Instant.now()
        );

        rideEventProducer.sendRideRequestedEvent(event);

        return new RideCreationResponseDTO(
                savedRide.getId().toString(),
                savedRide.getPassengerId().toString(),
                savedRide.getStatus(),
                savedRide.getCreatedAt()
        );
    }

    @Transactional
    public void driverAssigned(final DriverAssignedToRideEventDTO event) {

        final Ride ride = rideRepository.findById(UUID.fromString(event.rideId()))
                .orElseThrow(() -> new RideNotFoundException("Ride with id %s not found"));

        ride.setDriverId(UUID.fromString(event.driverId()));
        ride.setStatus(RideStatus.ACCEPTED);

        rideRepository.save(ride);
    }

    @Transactional
    public void acceptRide(final UUID driverId, final UUID rideId) {

        final Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Race with id %s not found."));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new BusinessRuleException("This race is not pending accessibility.");
        }

        if (!ride.getDriverId().equals(driverId)) {
            throw new BusinessRuleException("This driver is not assigned to this race.");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        final Ride savedRide = rideRepository.save(ride);

        final RideAcceptedByDriverEvent event = new RideAcceptedByDriverEvent(
                savedRide.getId().toString(),
                savedRide.getDriverId().toString(),
                Instant.now()
        );
        rideEventProducer.sendRideAcceptedEvent(event);
    }

    @Transactional
    public void rejectRide(final UUID driverId, final UUID rideId) {

        final Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Race with id %s not found"));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new BusinessRuleException("This race is not pending acceptance/rejection.");
        }

        if (!ride.getDriverId().equals(driverId)) {
            throw new BusinessRuleException("This driver is not assigned to this race.");
        }

        ride.setDriverId(null);
        ride.setStatus(RideStatus.REQUESTED);
        final Ride savedRide = rideRepository.save(ride);

        final RideRejectedByDriverEvent event = new RideRejectedByDriverEvent(
                savedRide.getId().toString(),
                driverId.toString(),
                Instant.now()
        );
        rideEventProducer.sendRideRejectEvent(event);
    }
}