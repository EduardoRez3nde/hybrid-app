package com.rezende.taxi_service.services;

import com.rezende.taxi_service.dto.RatingRequestDTO;
import com.rezende.taxi_service.entities.Ride;
import com.rezende.taxi_service.entities.RideRating;
import com.rezende.taxi_service.enums.RideStatus;
import com.rezende.taxi_service.event.DriverRatedEvent;
import com.rezende.taxi_service.exceptions.BusinessRuleException;
import com.rezende.taxi_service.exceptions.RideNotFoundException;
import com.rezende.taxi_service.repositories.RideRatingRepository;
import com.rezende.taxi_service.repositories.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RatingService {

    private final RideRepository rideRepository;
    private final RideEventProducer rideEventProducer;
    private final RideRatingRepository rideRatingRepository;

    public RatingService(
            final RideRepository rideRepository,
            final RideEventProducer rideEventProducer,
            final RideRatingRepository rideRatingRepository
    ) {
        this.rideRepository = rideRepository;
        this.rideEventProducer = rideEventProducer;
        this.rideRatingRepository = rideRatingRepository;
    }

    @Transactional
    public void submitRating(final RatingRequestDTO dto) {

        final Ride ride = rideRepository.findById(UUID.fromString(dto.rideId()))
                .orElseThrow(() -> new RideNotFoundException("Ride with id %s not found", dto.rideId()));

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new BusinessRuleException("Only completed races can be evaluated.");
        }

        final RideRating rideRating = RideRating.builder()
                .rideId(ride.getId())
                .driverId(ride.getDriverId())
                .passengerId(ride.getPassengerId())
                .rating(dto.rating())
                .comment(dto.comment())
                .build();

        rideRatingRepository.save(rideRating);
        rideEventProducer.sendDriverRatedEvent(DriverRatedEvent.of(ride, dto));
    }
}
