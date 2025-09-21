package com.rezende.matchmaking_service.entities;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import lombok.*;
import org.springframework.data.geo.Point;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PlanningEntity
public class RideRequest {

    @PlanningId
    private UUID rideId;

    private Point pickupLocation;

    @PlanningVariable(valueRangeProviderRefs = "availableDrivers")
    private MatchmakingDriver assignedDriver;
}