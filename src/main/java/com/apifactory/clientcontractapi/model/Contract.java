package com.apifactory.clientcontractapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apifactory.clientcontractapi.service.ContractService;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a contract associated with a client.
 * Includes start and end dates, cost amount, and last update timestamp.
 */
@Getter
@Setter
@Entity
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal costAmount;

    @JsonIgnore // do not expose the updateDate into the API
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    private static final Logger logger = LoggerFactory.getLogger(ContractService.class);


     /** Called before the entity is first persisted (insert). */
     @PrePersist
     protected void onCreate() {
         logger.info("ℹ️ PrePersist triggered for Contract!");
         if (startDate == null) {
             startDate = LocalDateTime.now();
         }
         updateDate = LocalDateTime.now();
     }

     /** Called before the entity is updated (update). */
     @PreUpdate
     protected void onUpdate() {
        logger.info("ℹ️ PreUpdate triggered for Contract!");
         updateDate = LocalDateTime.now();
     }
}