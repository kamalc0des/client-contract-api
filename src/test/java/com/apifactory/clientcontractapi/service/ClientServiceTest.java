package com.apifactory.clientcontractapi.service;

import com.apifactory.clientcontractapi.model.ClientType;
import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.model.Person;
import com.apifactory.clientcontractapi.repository.ClientRepository;
import com.apifactory.clientcontractapi.repository.ContractRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for {@link ClientService}.
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

    private Person testPerson;

    @BeforeEach
    void setup() {
        testPerson = new Person();
        testPerson.setName("Kamal Aarab");
        testPerson.setEmail("kamal@gmail.com");
        testPerson.setPhone("+84048120");
        testPerson.setBirthDate(LocalDate.of(1998, 8, 9));
        testPerson.setType(ClientType.PERSON);
        clientRepository.saveAndFlush(testPerson);
    }

    @Test
    void shouldCreateAndRetrieveClient() {
        Person saved = (Person) clientService.getClientById(testPerson.getId());
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Kamal Aarab");
    }

    @Test
    void shouldUpdateClientInfo() {
        testPerson.setPhone("+4129414124");
        clientService.updateClient(testPerson.getId(), testPerson);
        Person updated = (Person) clientService.getClientById(testPerson.getId());
        assertThat(updated.getPhone()).isEqualTo("+4129414124");
    }

    @Test
    void shouldThrowExceptionForUnknownClient() {
        UUID fakeId = UUID.randomUUID();
        assertThatThrownBy(() -> clientService.getClientById(fakeId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deletingClientShouldCloseActiveContracts() {

        // Create and persist a contract linked to this client
        Contract contract = new Contract();
        contract.setClient(testPerson);
        contract.setStartDate(LocalDateTime.now().minusDays(5));
        contract.setEndDate(null);
        contract.setCostAmount(new BigDecimal("100"));
        contractRepository.saveAndFlush(contract);

        // Act — delete client via service
        clientService.deleteClient(testPerson.getId());

        // Assert — all contracts should now have a non-null end date
        List<Contract> updatedContracts = contractRepository.findAll();
        assertThat(updatedContracts)
                .as("All active contracts should be closed after client deletion")
                .allMatch(c -> c.getEndDate() != null);
    }
}
