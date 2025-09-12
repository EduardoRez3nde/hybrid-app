package com.rezende.user_service.events;

import java.time.Instant;

public interface DomainEvent {

    String userId();
    Instant occurredAt();
}
