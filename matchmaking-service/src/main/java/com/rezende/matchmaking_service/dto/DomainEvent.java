package com.rezende.matchmaking_service.dto;

import java.time.Instant;

public interface DomainEvent {

    String userId();
    Instant occurredAt();
}
