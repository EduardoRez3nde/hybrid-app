package com.rezende.driver_service.dto;

import com.rezende.driver_service.enums.ApprovalStatus;

public record UpdateApprovalStatusRequestDTO(ApprovalStatus newStatus, String reason) { }
