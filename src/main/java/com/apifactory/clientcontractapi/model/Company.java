package com.apifactory.clientcontractapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a company client with a unique company identifier.
 */
@Getter
@Setter
@Entity
public class Company extends Client {

    @NotBlank
    @Pattern(
    regexp = "^[A-Za-z]{3}-\\d{3}$", message = "Invalid company identifier format. Expected pattern: aaa-123") // Optionnal but good to have, more clean ()
    @Column(unique = true, nullable = false) // Unique company id
    private String companyId;

    public Company() {
        setType(ClientType.COMPANY); // define the type of a Company
    }
}
