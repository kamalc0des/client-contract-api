package com.apifactory.clientcontractapi.service;

import com.apifactory.clientcontractapi.model.ClientType;
import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.model.Person;
import com.apifactory.clientcontractapi.repository.ClientRepository;
import com.apifactory.clientcontractapi.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for {@link ClientService}.
 * Verifies that deleting a client automatically closes all active contracts.
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    void deletingClientShouldCloseActiveContracts() {
        // Arrange — create and persist a Person client
        Person person = new Person();
        person.setName("Client To Delete");
        person.setEmail("delete@gmail.com");
        person.setPhone("+193228765");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setType(ClientType.PERSON);

        // Persist the client >> needed with H2 DATABASE
        clientRepository.saveAndFlush(person);

        // Create and persist a contract linked to this client
        Contract contract = new Contract();
        contract.setClient(person);
        contract.setStartDate(LocalDate.now().minusDays(5));
        contract.setEndDate(null);
        contract.setCostAmount(new BigDecimal("100"));
        contractRepository.saveAndFlush(contract);

        // Act — delete client via service
        clientService.deleteClient(person.getId());

        // Assert — all contracts should now have a non-null end date
        List<Contract> updatedContracts = contractRepository.findAll();
        assertThat(updatedContracts)
                .as("All active contracts should be closed after client deletion")
                .allMatch(c -> c.getEndDate() != null);
    }
}
