package com.apifactory.clientcontractapi.dto.contract;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ContractResponse {
    private Long id;
    private String clientName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal costAmount;
    private LocalDateTime updateDate;
}
