package com.rezende.driver_service.dto;

import com.rezende.driver_service.enums.OperationalStatus;

public record UpdateStatusRequest(OperationalStatus status) { }
