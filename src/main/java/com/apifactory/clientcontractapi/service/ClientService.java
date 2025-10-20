package com.apifactory.clientcontractapi.service;

import com.apifactory.clientcontractapi.model.Client;
import com.apifactory.clientcontractapi.model.ClientType;
import com.apifactory.clientcontractapi.model.Company;
import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.model.Person;
import com.apifactory.clientcontractapi.repository.ClientRepository;
import com.apifactory.clientcontractapi.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing client business logic.
 */
@Service
@Transactional // Do not remove, important to prevent error during actions on the class 
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;

    public ClientService(ClientRepository clientRepository, ContractRepository contractRepository) {
        this.clientRepository = clientRepository;
        this.contractRepository = contractRepository;
    }

    /**
     * Creates a new client.
     *
     * @param client the client to create
     * @return the persisted client entity
     */
    public Client createClient(Client client) {
        logger.info("Creating new client with name: {}", client.getName());
        return clientRepository.saveAndFlush(client);
    }

    /**
     * Retrieves all clients from the database.
     *
     * @return list of all clients
     */
    public List<Client> getAllClients() {
        logger.info("Fetching all clients from the database");
        return clientRepository.findAll();
    }

    /**
     * Retrieves a client by its unique identifier.
     *
     * @param id the client's UUID
     * @return the client entity if found
     * @throws IllegalArgumentException if the client does not exist
     */
    public Client getClientById(UUID id) {
        logger.info("Fetching client with ID: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + id));
    }

    /**
     * Updates an existing client's editable fields.
     * <p>
     * Note: birthDate and companyId cannot be modified once created.
     *
     * @param id            the client's UUID
     * @param updatedClient the client data to update
     * @return the updated client entity
     */
    public Client updateClient(UUID id, Client updatedClient) {
        Client existingClient = getClientById(id);

        // Preserve immutable fields
        updatedClient.setId(id);
        updatedClient.setType(existingClient.getType());

        if (existingClient.getType() == ClientType.PERSON && updatedClient instanceof Person updatedPerson) {
            Person existingPerson = (Person) existingClient;
            updatedPerson.setBirthDate(existingPerson.getBirthDate()); // preserved the birthDate, double protection (on service and model)
        }

        if (existingClient.getType() == ClientType.COMPANY && updatedClient instanceof Company updatedCompany) {
            Company existingCompany = (Company) existingClient;
            updatedCompany.setCompanyId(existingCompany.getCompanyId()); // preserved the companyId, double protection (on service and model)
        }

        logger.info("Updating client {} ({})", id, existingClient.getName());
        return clientRepository.save(updatedClient);
    }

    /**
     * Deletes a client and closes all their active contracts by setting their end
     * date.
     *
     * @param id the client's UUID
     */
    public void deleteClient(UUID id) {
        Client client = getClientById(id);
        logger.info("Deleting client {} ({}) and updating active contracts", id, client.getName());

        // Close all active contracts for this client
        List<Contract> activeContracts = contractRepository.findActiveContractsByClientId(id, null);
        for (Contract contract : activeContracts) {
            contract.setEndDate(LocalDateTime.now());
            contract.setClient(null); // detach client to avoid FK violation (error find during testing)
        }
        contractRepository.saveAll(activeContracts); // save the past contract from this client, but delete the client in DB
        contractRepository.flush(); // ensure DB state before client deletion

        clientRepository.delete(client);
        logger.info("Client {} deleted successfully with {} contracts closed", id, activeContracts.size());
    }
}
