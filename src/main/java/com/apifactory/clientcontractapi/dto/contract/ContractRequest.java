package com.apifactory.clientcontractapi.dto.contract;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Getter
@Setter
public class ContractRequest {
    @NotNull(message = "Client ID is required")
    private UUID clientId;
    
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @NotNull(message = "Cost amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost amount must be positive")
    private BigDecimal costAmount;
}
