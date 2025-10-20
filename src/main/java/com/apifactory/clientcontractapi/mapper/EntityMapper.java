package com.apifactory.clientcontractapi.mapper;

import com.apifactory.clientcontractapi.dto.client.*;
import com.apifactory.clientcontractapi.dto.contract.*;
import com.apifactory.clientcontractapi.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to convert between Entities and DTOs.
 */
public class EntityMapper {

    // === CLIENTS MAPPER ===
    public static ClientResponse toClientResponse(Client client) {
        ClientResponse dto = new ClientResponse();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setType(client.getType().name());

        if (client instanceof Person person) {
            dto.setBirthDate(person.getBirthDate());
        } else if (client instanceof Company company) {
            dto.setCompanyId(company.getCompanyId());
        }

        return dto;
    }

     /** Convert Entity → DTO (for creation) */
    public static List<ClientResponse> toClientResponseList(List<Client> clients) {
        return clients.stream().map(EntityMapper::toClientResponse).collect(Collectors.toList());
    }

    /** Convert DTO → Entity (for creation) */
    public static Client toClientEntity(ClientRequest dto) {
        if (dto == null) return null;

        if ("PERSON".equalsIgnoreCase(dto.getType())) {
            Person person = new Person();
            person.setType(ClientType.PERSON);
            person.setName(dto.getName());
            person.setEmail(dto.getEmail());
            person.setPhone(dto.getPhone());
            person.setBirthDate(dto.getBirthDate());
            return person;

        } else if ("COMPANY".equalsIgnoreCase(dto.getType())) {
            Company company = new Company();
            company.setType(ClientType.COMPANY);
            company.setName(dto.getName());
            company.setEmail(dto.getEmail());
            company.setPhone(dto.getPhone());
            company.setCompanyId(dto.getCompanyId());
            return company;
        }

        throw new IllegalArgumentException("Invalid client type: " + dto.getType());
    }

    // === CONTRACTS MAPPER ===
    public static ContractResponse toContractResponse(Contract contract) {
        ContractResponse dto = new ContractResponse();
        dto.setId(contract.getId());
        dto.setClientName(contract.getClient().getName());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setCostAmount(contract.getCostAmount());
        dto.setUpdateDate(contract.getUpdateDate());
        return dto;
    }

    /** Convert List<Entity> → List<DTO> */
    public static List<ContractResponse> toContractResponseList(List<Contract> contracts) {
        return contracts.stream().map(EntityMapper::toContractResponse).collect(Collectors.toList());
    }

    /** Convert DTO → Entity (for creation) */
    public static Contract toContractEntity(ContractRequest dto, Client client) {
        if (dto == null) return null;

        Contract contract = new Contract();
        contract.setClient(client);
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setCostAmount(dto.getCostAmount());
        return contract;
    }
}
