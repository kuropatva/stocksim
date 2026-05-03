package pl.kuropatva.stocksim.controller;

import org.springframework.web.bind.annotation.*;
import pl.kuropatva.stocksim.model.dto.web.StockListDto;
import pl.kuropatva.stocksim.service.BankStockService;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final BankStockService bankStockService;

    public StockController(BankStockService bankStockService) {
        this.bankStockService = bankStockService;
    }

    @GetMapping
    public StockListDto getStocks() {
        return bankStockService.getAllStocks();
    }

    @PostMapping
    public void setStocks(@RequestBody StockListDto stocks) {
        bankStockService.setAllStocks(stocks);
    }

}
