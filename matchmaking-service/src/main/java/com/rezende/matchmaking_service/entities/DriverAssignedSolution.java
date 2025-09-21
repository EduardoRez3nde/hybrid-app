package com.rezende.matchmaking_service.entities;

import ai.timefold.solver.core.api.domain.solution.*;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rezende.matchmaking_service.repositories.DriverRedisRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@PlanningSolution
public class DriverAssignedSolution {

    @PlanningEntityProperty
    private RideRequest rideRequest;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "availableDrivers")
    private List<MatchmakingDriver> matchmakingDrivers;

    @PlanningScore
    private HardSoftScore score;

    @ProblemFactProperty
    @JsonIgnore
    private DriverRedisRepository driverRedisRepository;

    public DriverAssignedSolution(
            final RideRequest rideRequest,
            final List<MatchmakingDriver> matchmakingDrivers,
            final HardSoftScore score,
            final DriverRedisRepository driverRedisRepository
    ) {
        this.rideRequest = rideRequest;
        this.matchmakingDrivers = matchmakingDrivers;
        this.score = score;
        this.driverRedisRepository = driverRedisRepository;
    }
}
