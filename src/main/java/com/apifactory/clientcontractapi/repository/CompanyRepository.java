package com.apifactory.clientcontractapi.repository;

import com.apifactory.clientcontractapi.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing {@link Company} entities.
 *
 * <p>Provides CRUD operations and an additional method for verifying
 * the uniqueness of a company identifier.</p>
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    /**
     * Checks if a company already exists with the given identifier.
     *
     * @param companyId the unique identifier of the company.
     * @return true if a company exists with this identifier, else false.
     */
    boolean existsByCompanyId(String companyId);
}
