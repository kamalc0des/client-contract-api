package com.apifactory.clientcontractapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Abstract base entity representing a Client (could be a Person or Company).
 * Each client has basic contact information (name, phone, email).
 */
@Getter // Use Getter/Setter to generate by default methods on attributes without writing them
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^[0-9+()\\-\\s]*$", message = "Invalid phone number format")
    private String phone;

    @Column(unique = true)
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private ClientType type;
}
