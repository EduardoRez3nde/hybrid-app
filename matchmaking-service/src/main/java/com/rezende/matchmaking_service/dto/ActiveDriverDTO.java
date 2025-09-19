package com.rezende.matchmaking_service.dto;

/**
 * DTO que representa a visão simplificada de um motorista ativo,
 * contendo apenas os dados necessários para o processo de matchmaking.
 * Este objeto é armazenado e recuperado do cache no Redis.
 *
 * @param id          O ID único do motorista.
 * @param longitude   A longitude da sua localização mais recente.
 * @param latitude    A latitude da sua localização mais recente.
 * @param rating      A avaliação média do motorista (ex: 4.8).
 * @param vehicleType O tipo de veículo (ex: "CAR", "MOTORCYCLE").
 */
public record ActiveDriverDTO(
        String id,
        double longitude,
        double latitude,
        double rating,
        String vehicleType
) {
}
