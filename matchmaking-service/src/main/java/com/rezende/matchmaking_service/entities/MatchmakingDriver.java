package com.rezende.matchmaking_service.entities;


import lombok.*;
import org.springframework.data.geo.Point;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchmakingDriver {
    private String id;
    private Point location;
    private double rating;
    private String vehicleType;
}