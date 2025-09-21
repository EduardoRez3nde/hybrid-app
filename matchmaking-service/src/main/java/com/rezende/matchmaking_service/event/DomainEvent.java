package com.rezende.matchmaking_service.event;

import java.time.Instant;

public interface DomainEvent {

    String getAggregateId();

    Instant getOccurredAt();
}