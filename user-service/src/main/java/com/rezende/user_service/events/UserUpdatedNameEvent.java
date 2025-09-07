package com.rezende.user_service.events;

import java.time.Instant;

public record UserUpdatedNameEvent(
        String userId,
        String newName,
        Instant eventTimestamp
) { }
