package com.apifactory.clientcontractapi.repository;

import com.apifactory.clientcontractapi.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing {@link Person} entities.
 *
 * <p>Provides CRUD operations for individual clients (persons).</p>
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
}
