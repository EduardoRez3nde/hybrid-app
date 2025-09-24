package com.rezende.notification_service.repositories;

import com.rezende.notification_service.entities.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    Optional<DeviceToken> findByToken(final String token);

    List<DeviceToken> findAllByUserId(final UUID userId);
}
