package com.rezende.driver_service.services;

import com.rezende.driver_service.dto.*;
import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.ApprovalStatus;
import com.rezende.driver_service.enums.OperationalStatus;
import com.rezende.driver_service.events.*;
import com.rezende.driver_service.exceptions.DriverNotFoundException;
import com.rezende.driver_service.exceptions.DriverPendingVerificationException;
import com.rezende.driver_service.mapper.DriverProfileFactory;
import com.rezende.driver_service.repositories.DriverProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class DriverService {

    private final DriverProfileRepository driverRepository;
    private final DriverEventProducer driverEventProducer;
    private final GeometryFactory geometryFactory;

    public DriverService(
            final DriverProfileRepository driverRepository,
            final DriverEventProducer driverEventProducer,
            final GeometryFactory geometryFactory
    ) {
        this.driverRepository = driverRepository;
        this.driverEventProducer = driverEventProducer;
        this.geometryFactory = geometryFactory;
    }

    @Transactional
    public void processNewUserEvent(final UserRegisterEventDTO event) {
        if (driverRepository.existsById(UUID.fromString(event.userId()))) {
            log.warn("Perfil de motorista para o userId: {} já existe. Ignorando evento de criação.", event.userId());
            return;
        }
        final DriverProfile driver = DriverProfileFactory.fromUserCreationEvent(event);
        driverRepository.save(driver);
        log.info("Perfil de motorista criado com sucesso para o userId: {}", event.userId());
    }

    @Transactional
    public void processVehicleApprovalEvent(final VehicleApprovedEventDTO event) {

        final DriverProfile driver = driverRepository
                .findById(UUID.fromString(event.driverId()))
                .orElseThrow(() -> {
                    log.error("Perfil de motorista não encontrado para o driverId: {}", event.driverId());
                    return new DriverNotFoundException("Driver not found for ID: %s", event.driverId());
                });

        driver.setHasApprovedVehicle(true);
        log.info("Motorista com ID {} agora tem um veículo aprovado.", driver.getUserId());
        driverRepository.save(driver);
    }

    @Transactional
    public DriverProfileResponseDTO submitOnboarding(final String userId, final OnboardDriverRequestDTO dto) {

        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %s not Found", userId));

        driver.setCnhNumber(dto.cnhNumber());

        final DriverProfile driverSave = driverRepository.save(driver);

        driverEventProducer.sendDriverOnboardingSubmittedEvent(DriverOnboardingSubmittedEvent.of(driverSave));

        return DriverProfileResponseDTO.of(driverSave);
    }

    @Transactional(readOnly = true)
    public DriverProfileResponseDTO getMyProfile(final String userId) {
        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %s not Found", userId));
        return DriverProfileResponseDTO.of(driver);
    }

    @Transactional
    public DriverProfileResponseDTO changeStatus(final String userId, final AccountStatusRequestDTO dto) {

        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %s not Found", userId));

        if (driver.getApprovalStatus() != ApprovalStatus.APPROVED
                && dto.status() == OperationalStatus.ONLINE) {
            throw new DriverPendingVerificationException("Driver is not approved and cannot go ONLINE");
        }

        driver.setOperationalStatus(dto.status());

        if (dto.latitude() != null && dto.longitude() != null) {
            final Point point = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));
            driver.setCurrentLocation(point);
        }

        final DriverProfile driverSave = driverRepository.save(driver);

        driverEventProducer.sendOperationalStatusChanged(DriverStatusUpdateEvent.of(driverSave));

        return DriverProfileResponseDTO.of(driverSave);
    }

    @Transactional
    public DriverProfileResponseDTO updateApprovalStatus(final String userId, final UpdateApprovalStatusRequestDTO dto) {

        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %s not Found", userId));

        if (driver.getApprovalStatus() != ApprovalStatus.PENDING_APPROVAL)
            throw new DriverPendingVerificationException("This driver is pending verification.");

        driver.setApprovalStatus(dto.newStatus());

        // TODO: adicionar a razão no banco

        final DriverProfile driverSave = driverRepository.save(driver);

        if (driver.getApprovalStatus() == ApprovalStatus.APPROVED)
            driverEventProducer.sendDriverApprovedEvent(DriverApprovedEvent.of(driverSave));
        else
            driverEventProducer.sendDriverRejectedEvent(DriverRejectedEvent.of(driverSave));

        return DriverProfileResponseDTO.of(driverSave);
    }

    @Transactional
    public void updateLocation(final String driverId, final CoordinatesDTO coordinates) {

        final DriverProfile driver = driverRepository.findById(UUID.fromString(driverId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %s not Found", driverId));

        driver.setCurrentLocation(geometryFactory.createPoint(new Coordinate(coordinates.longitude(), coordinates.latitude())));
        final DriverProfile driverSave = driverRepository.save(driver);

        driverEventProducer.sendDriverLocationEvent(DriverLocationEvent.of(driverSave));
    }

    @Transactional
    public void processDriverRatedEvent(final DriverRatedEventDTO event) {

        log.info("Recebido evento de avaliação para o motorista {}", event.driverId());

        final DriverProfile driver = driverRepository.findById(UUID.fromString(event.driverId()))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %s not found"));

        final double newAverage = calculateNewAverage(driver.getAverageRating(), driver.getTotalRatings(), event.rating());
        driver.setAverageRating(newAverage);
        driver.setTotalRatings(driver.getTotalRatings() + 1);
        driverRepository.save(driver);
        driverEventProducer.sendDriverAverageRating(DriverProfileUpdateEvent.of(driver));
    }

    public static double calculateNewAverage(final double oldAverage, final double totalRatings, final int rating) {
        return ((oldAverage * totalRatings) + rating) / (totalRatings + 1.0);
    }
}
