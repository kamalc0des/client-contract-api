package com.apifactory.clientcontractapi.repository;

import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.model.Person;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ContractRepositoryTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldReturnZeroWhenNoContractsExist() {
        UUID randomId = UUID.randomUUID();
        BigDecimal total = contractRepository.sumActiveContractsByClientId(randomId);
        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldFindActiveContractsAndSumCorrectly() {
        Person client = new Person();
        client.setName("Test Client");
        client.setEmail("test@gmail.com");
        client.setPhone("+343424114");
        client.setBirthDate(LocalDate.of(1995, 5, 10));
    
        // ✅ Persist and flush before reuse
        Person savedClient = clientRepository.saveAndFlush(client);
    
        Contract active = new Contract();
        active.setClient(savedClient);
        active.setCostAmount(new BigDecimal("100"));
        active.setStartDate(LocalDate.now().minusDays(5));
        active.setEndDate(null);
        contractRepository.save(active);
    
        Contract expired = new Contract();
        expired.setClient(savedClient);
        expired.setCostAmount(new BigDecimal("200"));
        expired.setEndDate(LocalDate.now().minusDays(1));
        contractRepository.save(expired);
    
        List<Contract> activeContracts = contractRepository.findActiveContractsByClientId(savedClient.getId(), null);
        assertThat(activeContracts).hasSize(1);
        assertThat(contractRepository.sumActiveContractsByClientId(savedClient.getId()))
                .isEqualByComparingTo("100");
    }    
}
