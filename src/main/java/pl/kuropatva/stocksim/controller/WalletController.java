package pl.kuropatva.stocksim.controller;


import org.springframework.web.bind.annotation.*;
import pl.kuropatva.stocksim.model.dto.web.TradeRequest;
import pl.kuropatva.stocksim.model.dto.web.WalletDto;
import pl.kuropatva.stocksim.service.TransactionService;
import pl.kuropatva.stocksim.service.WalletService;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final TransactionService transactionService;
    private final WalletService walletService;

    public WalletController(TransactionService transactionService, WalletService walletService) {
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    @PostMapping("/{walletId}/stocks/{stockName}")
    public void executeTrade(@PathVariable String walletId, @PathVariable String stockName, @RequestBody TradeRequest type) {
        transactionService.executeTrade(walletId, stockName, type);
    }

    @GetMapping("/{walletId}")
    public WalletDto getAllStocks(@PathVariable String walletId) {
        return walletService.getAllStocks(walletId);
    }

    @GetMapping("/{walletId}/stocks/{stockName}")
    public long getStockQuantity(@PathVariable String walletId, @PathVariable String stockName) {
        return walletService.getStockQuantity(walletId, stockName);

    }


}
