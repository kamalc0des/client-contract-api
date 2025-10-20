package com.apifactory.clientcontractapi.service;

import com.apifactory.clientcontractapi.model.ClientType;
import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.model.Person;
import com.apifactory.clientcontractapi.repository.ClientRepository;
import com.apifactory.clientcontractapi.repository.ContractRepository;

import jakarta.persistence.EntityManager;

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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ContractService}.
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ContractServiceTest {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private EntityManager entityManager;

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
    void shouldCreateContractWithDefaults() {
        Integer sizeContractsAlreadyCreated = contractRepository.findAll().size();
        Contract contract = contractService.createContract(testPerson.getId(),
                null, null, new BigDecimal("250.00"));

        assertThat(contract).isNotNull();
        assertThat(contractRepository.findAll()).hasSize(sizeContractsAlreadyCreated + 1);
        assertThat(contract.getStartDate()).isNotNull();
        assertThat(contract.getEndDate()).isNull();
        assertThat(contract.getUpdateDate()).isNotNull();
    }

    @Test
    void shouldUpdateContractCostAndUpdateDate() throws InterruptedException {
        Contract contract = contractService.createContract(testPerson.getId(),
                LocalDateTime.now().minusDays(10), null, new BigDecimal("100.00"));

        LocalDateTime oldUpdate = contract.getUpdateDate();

        contractService.updateCostAmount(contract.getId(), new BigDecimal("150.00"));
        entityManager.flush(); // send the update to the DB (update is sended, PreUpdate also)

        Contract updated = contractRepository.findById(contract.getId()).orElseThrow();

        assertThat(updated.getCostAmount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(updated.getUpdateDate()).isAfter(oldUpdate);
    }

    @Test
    void shouldReturnOnlyActiveContracts() {
        Contract active = contractService.createContract(testPerson.getId(),
                LocalDateTime.now().minusDays(5), null, new BigDecimal("100.00"));
        Contract closed = contractService.createContract(testPerson.getId(),
                LocalDateTime.now().minusDays(30), LocalDateTime.now().minusDays(1), new BigDecimal("80.00"));

        List<Contract> activeContracts = contractService.getActiveContracts(testPerson.getId(), null);
        assertThat(activeContracts).contains(active).doesNotContain(closed);
    }

    @Test
    void shouldSumOnlyActiveContracts() {
        contractService.createContract(testPerson.getId(),
                LocalDateTime.now().minusDays(5), null, new BigDecimal("120.00"));
        contractService.createContract(testPerson.getId(),
                LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(5), new BigDecimal("50.00"));

        BigDecimal sum = contractService.getTotalActiveContractAmount(testPerson.getId());
        assertThat(sum).isEqualTo(new BigDecimal("120.00"));
    }
}
