package com.rezende.notification_service.event;

import java.time.Instant;

public record DriverAssignedEvent(
        String rideId,
        String driverId,
        // TODO: incluir dados extras, como o tempo estimado
        // TODO: e o valor da corrida para mostrar na notificação.
        Instant assignedAt
) {}