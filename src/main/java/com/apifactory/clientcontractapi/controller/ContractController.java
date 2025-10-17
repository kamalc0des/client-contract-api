package com.apifactory.clientcontractapi.controller;

import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.service.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing contracts and related operations.
 */
@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody Contract contract) {
        return ResponseEntity.ok(contractService.createContract(contract));
    }

    @PutMapping("/{id}/cost")
    public ResponseEntity<Contract> updateCost(@PathVariable Long id, @RequestParam BigDecimal newAmount) {
        return ResponseEntity.ok(contractService.updateCostAmount(id, newAmount));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Contract>> getContracts(
            @PathVariable UUID clientId,
            @RequestParam(required = false) LocalDate updateDate) {
        return ResponseEntity.ok(contractService.getActiveContracts(clientId, updateDate));
    }

    @GetMapping("/client/{clientId}/sum")
    public ResponseEntity<BigDecimal> getContractSum(@PathVariable UUID clientId) {
        return ResponseEntity.ok(contractService.getTotalActiveContractAmount(clientId));
    }
}
