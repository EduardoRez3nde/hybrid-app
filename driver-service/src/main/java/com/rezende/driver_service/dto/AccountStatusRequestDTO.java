package com.rezende.driver_service.dto;

import com.rezende.driver_service.enums.OperationalStatus;

public record AccountStatusRequestDTO(
        OperationalStatus status,
        Double latitude,
        Double longitude
) {}
