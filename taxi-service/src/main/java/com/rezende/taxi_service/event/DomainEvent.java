package com.rezende.taxi_service.event;

import java.time.Instant;

public interface DomainEvent {

    String getAggregateId();

    Instant getOccurredAt();
}