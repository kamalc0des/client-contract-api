package com.apifactory.clientcontractapi.controller;

import com.apifactory.clientcontractapi.model.Contract;
import com.apifactory.clientcontractapi.service.ContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for {@link ContractController}.
 * Verifies that endpoints return correct JSON responses and interact properly with the service layer.
 */
@WebMvcTest(ContractController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security for the test (no token needed)
@ActiveProfiles("test")
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService contractService;

    /**
     * Verifies that calling the endpoint to fetch active contracts
     * for a given client returns a 200 OK response and a JSON array.
     */
    @Test
    void shouldReturnActiveContractsForClient() throws Exception {
        UUID clientId = UUID.randomUUID();

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setCostAmount(new BigDecimal("150.0"));
        contract.setStartDate(LocalDate.now().minusDays(10));
        contract.setEndDate(null);

        // Mock the service layer response
        when(contractService.getActiveContracts(clientId, null))
                .thenReturn(List.of(contract));

        // Perform GET request and validate response
        mockMvc.perform(get("/api/contracts/client/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // must be status = 200
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].costAmount").value(150.00)); // same contract as created before
    }
}
