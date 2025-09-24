package com.rezende.notification_service.services;

import com.rezende.notification_service.dto.DeviceRegisteredEventDTO;
import com.rezende.notification_service.entities.DeviceToken;
import com.rezende.notification_service.repositories.DeviceTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DeviceTokenService {

    private final DeviceTokenRepository deviceRepository;

    public DeviceTokenService(final DeviceTokenRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public void registerOrUpdateToken(final DeviceRegisteredEventDTO dto) {

        final String tokenValue = dto.deviceToken();
        final UUID userId = UUID.fromString(dto.userId());

        final Optional<DeviceToken> existingToken = deviceRepository.findByToken(tokenValue);

        if (existingToken.isPresent()) {
            log.info("Token {} já existe. A atualizar o utilizador e o timestamp.", tokenValue);
            final DeviceToken tokenToUpdate = existingToken.get();

            tokenToUpdate.setUserId(userId);
            tokenToUpdate.setUpdatedAt(Instant.now());

            deviceRepository.save(tokenToUpdate);
        } else {
            log.info("A registar novo token para o utilizador {}", userId);
            final DeviceToken newDeviceToken = DeviceToken.builder()
                    .userId(userId)
                    .token(tokenValue)
                    .build();
            deviceRepository.save(newDeviceToken);
        }
    }
}
