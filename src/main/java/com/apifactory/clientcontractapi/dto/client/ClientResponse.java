package com.apifactory.clientcontractapi.dto.client;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ClientResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String type;
    private LocalDate birthDate;
    private String companyId;
}
