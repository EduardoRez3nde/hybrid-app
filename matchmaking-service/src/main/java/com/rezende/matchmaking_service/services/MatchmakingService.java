package com.rezende.matchmaking_service.services;

import ai.timefold.solver.core.api.solver.SolverManager;
import com.rezende.matchmaking_service.dto.ActiveDriverDTO;
import com.rezende.matchmaking_service.dto.RideRequestedEventDTO;
import com.rezende.matchmaking_service.entities.DriverAssignedSolution;
import com.rezende.matchmaking_service.entities.MatchmakingDriver;
import com.rezende.matchmaking_service.entities.RideRequest;
import com.rezende.matchmaking_service.event.DriverAssignedToRideEvent;
import com.rezende.matchmaking_service.event.NoDriverFoundEvent;
import com.rezende.matchmaking_service.repositories.DriverRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class MatchmakingService {

    private final DriverRedisRepository driverRedisRepository;
    private final SolverManager<DriverAssignedSolution, UUID> solverManager;
    private final MatchmakingEventProducer matchmakingEventProducer;

    public MatchmakingService(
            final DriverRedisRepository driverRedisRepository,
            final SolverManager<DriverAssignedSolution, UUID> solverManager,
            final MatchmakingEventProducer matchmakingEventProducer
    ) {
        this.driverRedisRepository = driverRedisRepository;
        this.solverManager = solverManager;
        this.matchmakingEventProducer = matchmakingEventProducer;
    }

    @Transactional
    public void findBestDriverForRide(final RideRequestedEventDTO event) {

        final List<String> driversOnline = driverRedisRepository.findNearbyDriverIds(
                new Point(event.origin().longitude(), event.origin().latitude()),
                5000
        );

        if (driversOnline.isEmpty()) {
            log.warn("Nenhum motorista encontrado para a corrida {}", event.rideId());
            matchmakingEventProducer.sendNoDriverFoundEvent(new NoDriverFoundEvent(event.rideId(), Instant.now()));
            return;
        }

        final List<ActiveDriverDTO> activeDrivers = driverRedisRepository.findAllByIds(driversOnline);

        final List<MatchmakingDriver> matchmakingDrivers = activeDrivers.stream()
                .map(driver -> MatchmakingDriver.builder()
                        .id(driver.id())
                        .location(new Point(driver.longitude(), driver.latitude()))
                        .rating(driver.rating())
                        .vehicleType(driver.vehicleType())
                        .build()
                ).toList();

        final RideRequest rideRequest = RideRequest.builder()
                .rideId(UUID.fromString(event.rideId()))
                .pickupLocation(new Point(event.origin().longitude(), event.origin().latitude()))
                .build();

        final DriverAssignedSolution driverAssignedSolution = DriverAssignedSolution.builder()
                .matchmakingDrivers(matchmakingDrivers)
                .rideRequest(rideRequest)
                .driverRedisRepository(driverRedisRepository)
                .build();

        try {
            final DriverAssignedSolution solution = solverManager.solve(UUID.randomUUID(), driverAssignedSolution).getFinalBestSolution();
            final MatchmakingDriver bestDriver = solution.getRideRequest().getAssignedDriver();

            if (bestDriver != null) {
                log.info("Matchmaking concluído para a corrida {}. Melhor motorista: {}", event.rideId(), bestDriver.getId());
                matchmakingEventProducer.sendDriverAssignedRideEvent(new DriverAssignedToRideEvent(event.rideId(), bestDriver.getId(), Instant.now()));
            } else {
                log.warn("O solver não conseguiu atribuir um motorista para a corrida {}", event.rideId());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
