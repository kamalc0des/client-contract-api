package com.apifactory.clientcontractapi.controller;

import com.apifactory.clientcontractapi.dto.contract.ContractRequest;
import com.apifactory.clientcontractapi.dto.contract.ContractResponse;
import com.apifactory.clientcontractapi.mapper.EntityMapper;
import com.apifactory.clientcontractapi.model.Client;
import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.service.ClientService;
import com.apifactory.clientcontractapi.service.ContractService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing contracts and related operations.
 */
@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private static final Logger logger = LoggerFactory.getLogger(ContractController.class);

    private final ContractService contractService;
    private final ClientService clientService;

    public ContractController(ContractService contractService, ClientService clientService) {
        this.contractService = contractService;
        this.clientService = clientService;
    }

    /**
     * Create a new contract for a client.
     */
    @PostMapping
    public ResponseEntity<ContractResponse> createContract(@RequestBody ContractRequest request) {
        logger.info("POST /api/contracts - Creating contract for client {}", request.getClientId());
        Client client = clientService.getClientById(request.getClientId());
        Contract contract = EntityMapper.toContractEntity(request, client);
        Contract saved = contractService.createContract(contract);
        return ResponseEntity.ok(EntityMapper.toContractResponse(saved));
    }

    /**
     * Update contract cost amount.
     */
    @PutMapping("/{id}/cost")
    public ResponseEntity<ContractResponse> updateCostAmount(@PathVariable Long id, @RequestParam BigDecimal newAmount) {
        logger.info("PUT /api/contracts/{}/cost - Updating cost to {}", id, newAmount);
        Contract updated = contractService.updateCostAmount(id, newAmount);
        return ResponseEntity.ok(EntityMapper.toContractResponse(updated));
    }

    /**
     * Get all active contracts for one client (with optional updateDate filter).
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ContractResponse>> getActiveContracts(
            @PathVariable UUID clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime updateDate) {

        logger.info("GET /api/contracts/client/{}?updateDate={} - Fetching active contracts", clientId, updateDate);
        List<ContractResponse> response = EntityMapper.toContractResponseList(
                contractService.getActiveContracts(clientId, updateDate)
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Get total active contract amount for one client.
     */
    @GetMapping("/client/{clientId}/total")
    public ResponseEntity<BigDecimal> getTotalActiveContractAmount(@PathVariable UUID clientId) {
        logger.info("GET /api/contracts/client/{}/total - Fetching total active amount", clientId);
        BigDecimal total = contractService.getTotalActiveContractAmount(clientId);
        return ResponseEntity.ok(total);
    }
}
