package com.rezende.driver_service.services;

import com.rezende.driver_service.dto.DriverProfileResponse;
import com.rezende.driver_service.dto.OnboardDriverRequestDTO;
import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.ApprovalStatus;
import com.rezende.driver_service.events.DriverApprovedEvent;
import com.rezende.driver_service.events.DriverOnboardingSubmittedEvent;
import com.rezende.driver_service.events.UserRegisterEvent;
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
    public void processNewUserEvent(final UserRegisterEvent event) {

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
    public DriverProfileResponse approveDriver(final String userId) {

        final DriverProfile driver = driverRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DriverNotFoundException("Driver with id %d not Found", userId));

        if (driver.getApprovalStatus() != ApprovalStatus.PENDING_APPROVAL)
            throw new DriverNotPendingVerificationException("This driver is not pending verification.");

        driver.setApprovalStatus(ApprovalStatus.APPROVED);

        final DriverProfile driverSave = driverRepository.save(driver);

        driverEventProducer.sendDriverApprovedEvent(DriverApprovedEvent.of(driverSave));

        return DriverProfileResponse.of(driverSave);
    }
}
