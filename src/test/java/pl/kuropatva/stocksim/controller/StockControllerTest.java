package pl.kuropatva.stocksim.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kuropatva.stocksim.model.dto.web.StockDto;
import pl.kuropatva.stocksim.model.dto.web.StockListDto;
import pl.kuropatva.stocksim.service.BankStockService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
@ActiveProfiles("test")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankStockService bankStockService;

    @Test
    void shouldGetBankStocks() throws Exception {
        when(bankStockService.getAllStocks()).thenReturn(new StockListDto(List.of(
                new StockDto("AAPL", 100)
        )));

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks[0].name").value("AAPL"));
    }
}