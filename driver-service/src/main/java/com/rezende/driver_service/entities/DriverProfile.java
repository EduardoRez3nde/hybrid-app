package com.rezende.driver_service.entities;

import com.rezende.driver_service.enums.ApprovalStatus;
import com.rezende.driver_service.enums.OperationalStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "tb_driver_profile")
public class DriverProfile {

    @Id
    private UUID id;
    private String cnhNumber;
    private ApprovalStatus approvalStatus;
    private OperationalStatus operationalStatus;
}
