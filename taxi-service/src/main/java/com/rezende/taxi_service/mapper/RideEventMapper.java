package com.rezende.taxi_service.mapper;

import com.rezende.taxi_service.dto.LocationDTO;
import com.rezende.taxi_service.dto.RideRequestedEventDTO;
import com.rezende.taxi_service.entities.Location;
import com.rezende.taxi_service.event.RideRequestedEvent;

public class RideEventMapper {

    public static RideRequestedEventDTO toDTO(RideRequestedEvent event) {
        return new RideRequestedEventDTO(
                event.rideId().toString(),
                event.passengerId().toString(),
                toDTO(event.origin()),
                toDTO(event.destination()),
                event.eventTimestamp()
        );
    }

    public static LocationDTO toDTO(Location location) {
        return new LocationDTO(
                location.getCoordinates().getY(), // latitude
                location.getCoordinates().getX()  // longitude
        );
    }
}
