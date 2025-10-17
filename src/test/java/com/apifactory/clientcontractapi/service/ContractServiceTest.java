package com.apifactory.clientcontractapi.service;

import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractService contractService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldUpdateCostAmountAndTriggerSave() {
        Contract contract = new Contract();
        contract.setId(1L);
        contract.setCostAmount(new BigDecimal("500"));

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenAnswer(i -> i.getArgument(0));

        Contract result = contractService.updateCostAmount(1L, new BigDecimal("750"));

        assertThat(result.getCostAmount()).isEqualByComparingTo("750");
        verify(contractRepository, times(1)).save(contract);
    }
}
