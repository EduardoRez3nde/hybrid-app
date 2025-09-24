package com.rezende.notification_service.dto;

import java.time.Instant;

public record DeviceRegisteredEventDTO(
        String userId,
        String deviceToken,
        Instant registeredAt
) { }