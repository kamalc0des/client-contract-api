package com.apifactory.clientcontractapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private LocalDate startDate;
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal costAmount;

    @JsonIgnore // do not expose the updateDate into the API
    private LocalDate updateDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    /**
     * JPA entity listener used to automatically manage timestamps.
     */
    public static class AuditListener {

        /** Called before the entity is first persisted (insert). */
        @PrePersist
        public void prePersist(Contract contract) {
            if (contract.getStartDate() == null) {
                contract.setStartDate(LocalDate.now());
            }
            contract.setUpdateDate(LocalDate.now());
        }

        /** Called before the entity is updated (update). */
        @PreUpdate
        public void preUpdate(Contract contract) {
            contract.setUpdateDate(LocalDate.now());
        }
    }
}