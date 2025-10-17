package com.apifactory.clientcontractapi.service;

import com.apifactory.clientcontractapi.model.Company;
import com.apifactory.clientcontractapi.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing Company-type clients.
 */
@Service
@Transactional // Do not remove, important to prevent error during actions on the class 
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Creates a new company after ensuring its identifier is unique.
     *
     * @param company the company entity to save
     * @return the saved company
     * @throws IllegalArgumentException if the identifier already exists
     */
    public Company createCompany(Company company) {
        if (companyRepository.existsByCompanyId(company.getCompanyId())) {
            throw new IllegalArgumentException("Company identifier already exists: " + company.getCompanyId());
        }
        logger.info("Creating company client: {}", company.getName());
        return companyRepository.save(company);
    }

    /**
     * Retrieves all companies.
     *
     * @return list of companies
     */
    public List<Company> getAllCompanies() {
        logger.info("Fetching all companies from database");
        return companyRepository.findAll();
    }

    /**
     * Retrieves a company by ID.
     *
     * @param id the company's UUID
     * @return the company if found
     */
    public Company getCompanyById(UUID id) {
        logger.info("Fetching company with ID: {}", id);
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + id));
    }

    /**
     * Deletes a company by ID.
     *
     * @param id the company's UUID
     */
    public void deleteCompany(UUID id) {
        logger.info("Deleting company with ID: {}", id);
        companyRepository.deleteById(id);
    }
}
