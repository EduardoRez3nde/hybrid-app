package com.rezende.driver_service.services;

import com.rezende.driver_service.dto.*;
import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.ApprovalStatus;
import com.rezende.driver_service.events.*;
import com.rezende.driver_service.exceptions.DriverNotPendingVerificationException;
import com.rezende.driver_service.exceptions.UserEventProcessingException;
import com.rezende.driver_service.exceptions.DriverNotFoundException;
import com.rezende.driver_service.mapper.DriverProfileFactory;
import com.rezende.driver_service.repositories.DriverProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class DriverService {

    private final DriverProfileRepository driverRepository;
    private final DriverEventProducer driverEventProducer;

    public DriverService(
            final DriverProfileRepository driverRepository,
            final DriverEventProducer driverEventProducer
    ) {
        this.driverRepository = driverRepository;
        this.driverEventProducer = driverEventProducer;
    }

    @Transactional
    public void processNewUserEvent(final UserRegisterEventDTO event) {

        if (driverRepository.existsById(UUID.fromString(event.userId()))) return;

        try {
            final DriverProfile driver = DriverProfileFactory.fromUserCreationEvent(event);
            driverRepository.save(driver);
        } catch (Exception e) {
            throw new UserEventProcessingException("Error processing UserCreatedEvent for userId: " + event.userId(), e);
        }
    }

    @Transactional
    public DriverProfileResponse submitOnboarding(final String userId, final OnboardDriverRequestDTO dto) {

        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %d not Found", userId));

        driver.setCnhNumber(dto.cnhNumber());

        final DriverProfile driverSave = driverRepository.save(driver);

        driverEventProducer.sendDriverOnboardingSubmittedEvent(DriverOnboardingSubmittedEvent.of(driverSave));

        return DriverProfileResponse.of(driverSave);
    }

    @Transactional(readOnly = true)
    public DriverProfileResponse getMyProfile(final String userId) {
        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %d not Found", userId));
        return DriverProfileResponse.of(driver);
    }

    @Transactional
    public DriverProfileResponse changeStatus(final String userId, final AccountStatusRequestDTO dto) {

        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %d not Found", userId));

        driver.setOperationalStatus(dto.status());

        final DriverProfile driverSave = driverRepository.save(driver);

        driverEventProducer.sendOperationalStatusChanged(DriverOperationalStatusChangedEvent.of(driverSave));

        return DriverProfileResponse.of(driverSave);
    }

    @Transactional
    public DriverProfileResponse updateApprovalStatus(final String userId, final UpdateApprovalStatusRequestDTO dto) {

        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %d not Found", userId));

        if (driver.getApprovalStatus() != ApprovalStatus.PENDING_APPROVAL)
            throw new DriverNotPendingVerificationException("This driver is not pending verification.");

        driver.setApprovalStatus(dto.newStatus());

        // TODO: adicionar a razão no banco

        final DriverProfile driverSave = driverRepository.save(driver);

        if (driver.getApprovalStatus() == ApprovalStatus.APPROVED)
            driverEventProducer.sendDriverApprovedEvent(DriverApprovedEvent.of(driverSave));
        else
            driverEventProducer.sendDriverRejectedEvent(DriverRejectedEvent.of(driverSave));

        return DriverProfileResponse.of(driverSave);
    }
}
