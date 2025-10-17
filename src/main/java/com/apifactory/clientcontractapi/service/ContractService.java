package com.apifactory.clientcontractapi.service;

import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing contract business logic.
 */
@Service
@Transactional // Do not remove, important to prevent error during actions on the class 
public class ContractService {

    private static final Logger logger = LoggerFactory.getLogger(ContractService.class);
    private final ContractRepository contractRepository;

    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    /**
     * Creates a new contract for a client.
     * If no start date is provided, the current date is used.
     *
     * @param contract the contract entity to save
     * @return the persisted contract
     */
    public Contract createContract(Contract contract) {
        logger.info("Creating contract for client {}", contract.getClient().getId());
        return contractRepository.save(contract);
    }

    /**
     * Updates a contract's cost amount and automatically refreshes its updateDate.
     *
     * @param id the contract ID
     * @param newAmount the new cost amount
     * @return the updated contract
     */
    public Contract updateCostAmount(Long id, BigDecimal newAmount) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        contract.setCostAmount(newAmount);
        logger.info("Updated contract {} with new cost amount {}", id, newAmount);
        return contractRepository.save(contract);
    }

    /**
     * Retrieves active contracts for a client, optionally filtered by update date.
     *
     * @param clientId   the UUID of the client
     * @param updateDate optional filter; if null, all active contracts are returned
     * @return list of active contracts
     */
    public List<Contract> getActiveContracts(UUID clientId, LocalDate updateDate) {
        if (updateDate != null) {
            logger.info("Fetching active contracts for client {} updated since {}", clientId, updateDate);
        } else {
            logger.info("Fetching all active contracts for client {}", clientId);
        }
        return contractRepository.findActiveContractsByClientId(clientId, updateDate);
    }

    /**
     * Calculates the total sum of active contract amounts for a given client.
     *
     * @param clientId the client ID
     * @return total active contract amount
     */
    public BigDecimal getTotalActiveContractAmount(UUID clientId) {
        logger.info("Calculating total active contract cost for client {}", clientId);
        return contractRepository.sumActiveContractsByClientId(clientId);
    }
}

