package com.apifactory.clientcontractapi.dto.client;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class ClientRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{6,15}$", message = "Phone number must be valid")
    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Client type is required")
    @Pattern(regexp = "PERSON|COMPANY", message = "Type must be PERSON or COMPANY")
    private String type;

    // For PERSON
    private LocalDate birthDate;

    // For COMPANY
    private String companyId;
}
