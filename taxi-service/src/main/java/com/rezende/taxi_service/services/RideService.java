package com.rezende.taxi_service.services;

import com.rezende.taxi_service.dto.RequestRideDTO;
import com.rezende.taxi_service.dto.RideCreationResponseDTO;
import com.rezende.taxi_service.entities.Location;
import com.rezende.taxi_service.entities.Ride;
import com.rezende.taxi_service.enums.RideStatus;
import com.rezende.taxi_service.event.RideRequestedEvent;
import com.rezende.taxi_service.repositories.RideRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RideEventProducer rideEventProducer;

    public RideService(
            final RideRepository rideRepository,
            final RideEventProducer rideEventProducer
    ) {
        this.rideRepository = rideRepository;
        this.rideEventProducer = rideEventProducer;
    }

    /**
     * Orquestra a solicitação de uma nova corrida.
     * Este método é transacional, garantindo que a criação da corrida e a publicação
     * do evento ocorram de forma atomica (embora a entrega do evento seja assíncrona).
     *
     * @param passengerId O ID do passageiro que esta a solicitar a corrida (vindo do token).
     * @param dto Os dados da requisição (origem e destino).
     * @return Um DTO com a confirmação de que o pedido foi aceite para processamento.
     */
    @Transactional
    public RideCreationResponseDTO requestRide(final String passengerId, final RequestRideDTO dto) {

        log.info("Recebido pedido de corrida do passageiro: {}", passengerId);

        final Ride newRide = Ride.builder()
                .passengerId(UUID.fromString(passengerId))
                .origin(Location.from(dto.origin().latitude(), dto.origin().longitude()))
                .destination(Location.from(dto.destination().latitude(), dto.destination().longitude()))
                .status(RideStatus.REQUESTED)
                .build();

        final Ride savedRide = rideRepository.save(newRide);
        log.info("Corrida criada com ID: {} e status REQUESTED.", savedRide.getId());

        final RideRequestedEvent event = new RideRequestedEvent(
                savedRide.getId(),
                savedRide.getPassengerId(),
                savedRide.getOrigin(),
                savedRide.getDestination(),
                Instant.now()
        );

        rideEventProducer.sendRideRequestedEvent(event);

        return new RideCreationResponseDTO(
                savedRide.getId().toString(),
                savedRide.getPassengerId().toString(),
                savedRide.getStatus(),
                savedRide.getCreatedAt()
        );
    }
}
