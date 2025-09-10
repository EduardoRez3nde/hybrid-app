package com.rezende.driver_service.dto;

import com.rezende.driver_service.enums.AccountStatus;
import com.rezende.driver_service.enums.RoleType;
import com.rezende.driver_service.events.DomainEvent;

import java.time.Instant;

public record UserRegisterEventDTO(
        String userId,
        String name,
        String email,
        RoleType roleType,
        AccountStatus accountStatus,
        Instant eventTimestamp
) implements DomainEvent {

    @Override
    public Instant occurredAt() {
        return eventTimestamp;
    }
}
