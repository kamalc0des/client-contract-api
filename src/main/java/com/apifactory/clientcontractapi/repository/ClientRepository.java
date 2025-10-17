package com.apifactory.clientcontractapi.repository;

import com.apifactory.clientcontractapi.model.Client;
import com.apifactory.clientcontractapi.model.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing {@link Client} entities.
 *
 * <p>Provides database operations for all client types
 * (Person and Company) and allows filtering by {@link ClientType}.</p>
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    /**
     * Finds all clients by their type (PERSON or COMPANY).
     *
     * @param type the client type to filter by
     * @return a list of clients matching the given type (PERSON or COMPANY)
     */
    List<Client> findByType(ClientType type);
}
