package com.apifactory.clientcontractapi.controller;

import com.apifactory.clientcontractapi.dto.client.ClientRequest;
import com.apifactory.clientcontractapi.dto.client.ClientResponse;
import com.apifactory.clientcontractapi.mapper.EntityMapper;
import com.apifactory.clientcontractapi.model.Client;
import com.apifactory.clientcontractapi.service.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for generic client operations (Person or Company).
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Create a new client (Person or Company).
     */
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@RequestBody ClientRequest request) {
        logger.info("POST /api/clients - Creating new client {}", request.getName());
        Client client = EntityMapper.toClientEntity(request);
        Client saved = clientService.createClient(client);
        return ResponseEntity.ok(EntityMapper.toClientResponse(saved));
    }

    /**
     * Update an existing client (except birthDate and companyId).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable UUID id, @RequestBody ClientRequest request) {
        logger.info("PUT /api/clients/{} - Updating client", id);
        Client client = EntityMapper.toClientEntity(request);
        Client updated = clientService.updateClient(id, client);
        return ResponseEntity.ok(EntityMapper.toClientResponse(updated));
    }

    /**
     * Delete a client and close their active contracts.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        logger.info("DELETE /api/clients/{} - Deleting client", id);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all clients.
     */
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        logger.info("GET /api/clients - Fetching all clients");
        List<ClientResponse> response = EntityMapper.toClientResponseList(clientService.getAllClients());
        return ResponseEntity.ok(response);
    }

    /**
     * Get one client by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable UUID id) {
        logger.info("GET /api/clients/{} - Fetching client", id);
        Client client = clientService.getClientById(id);
        return ResponseEntity.ok(EntityMapper.toClientResponse(client));
    }
}
