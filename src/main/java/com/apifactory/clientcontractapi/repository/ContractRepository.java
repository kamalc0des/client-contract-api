package com.apifactory.clientcontractapi.repository;

import com.apifactory.clientcontractapi.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing {@link Contract} entities.
 *
 * <p>Provides CRUD operations and custom queries for retrieving active contracts
 * and calculating total costs for specific clients.</p>
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * Retrieves all active contracts for a specific client.
     * A contract is considered active if : endDate == null or endDate > current date.
     *
     * @param clientId the UUID of the client.
     * @param updateDate optionnal filter; if null, all active contracts are returned.
     * @return a list of active contracts associated with the given client.
     */
    @Query("""
        SELECT c FROM Contract c
        WHERE c.client.id = :clientId
        AND (c.endDate IS NULL OR c.endDate > CURRENT_DATE)
        AND (:updateDate IS NULL OR c.updateDate >= :updateDate)
    """)    
    List<Contract> findActiveContractsByClientId(UUID clientId, LocalDate updateDate);

    /**
     * Calculates the total cost of all active contracts for a specific client.
     *
     * @param clientId the UUID of the client.
     * @return the sum of all active contract cost amounts, or 0 if none exist.
     */
    @Query("""
        SELECT COALESCE(SUM(c.costAmount), 0) FROM Contract c 
        WHERE c.client.id = :clientId 
        AND (c.endDate IS NULL OR c.endDate > CURRENT_DATE)
    """)
    BigDecimal sumActiveContractsByClientId(UUID clientId);
}
