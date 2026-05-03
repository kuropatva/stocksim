package pl.kuropatva.stocksim.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.kuropatva.stocksim.controller.WalletController;
import pl.kuropatva.stocksim.exception.InsufficientStockException;
import pl.kuropatva.stocksim.exception.StockNotFoundException;
import pl.kuropatva.stocksim.model.dto.web.TradeRequest;
import pl.kuropatva.stocksim.model.entity.Stock;
import pl.kuropatva.stocksim.repository.StockRepository;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private WalletController walletController;

    private static final String WALLET_ID = "test-wallet";
    private static final String STOCK_NAME = "TEST";

    @BeforeEach
    void setUp() {
        stockRepository.deleteAll();

        // Setup bank stock
        Stock bankStock = new Stock(STOCK_NAME);
        bankStock.setQuantity(5);
        bankStock.setWallet(null);
        stockRepository.save(bankStock);
    }

    @Test
    void shouldBuyStockSuccessfully() {
        transactionService.executeTrade(WALLET_ID, STOCK_NAME, new TradeRequest("buy"));

        assertEquals(1, getWalletQty(WALLET_ID, STOCK_NAME));
        assertEquals(4, getBankQty(STOCK_NAME));
    }

    @Test
    void shouldSellStockSuccessfully() {
        transactionService.executeTrade(WALLET_ID, STOCK_NAME, new TradeRequest("buy"));
        transactionService.executeTrade(WALLET_ID, STOCK_NAME, new TradeRequest("sell"));

        assertEquals(0, getWalletQty(WALLET_ID, STOCK_NAME));
        assertEquals(5, getBankQty(STOCK_NAME));
    }

    @Test
    void shouldThrowWhenStockDoesNotExist() {
        assertThrows(StockNotFoundException.class, () ->
                transactionService.executeTrade(WALLET_ID, "NONEXISTENT", new TradeRequest("buy"))
        );
    }

    @Test
    void shouldThrowWhenNoStockInBankForBuy() {
        // Deplete bank
        Stock bank = stockRepository.findByNameAndWalletIsNull(STOCK_NAME).get();
        bank.setQuantity(0);
        stockRepository.save(bank);

        assertThrows(InsufficientStockException.class, () ->
                transactionService.executeTrade(WALLET_ID, STOCK_NAME, new TradeRequest("buy"))
        );
    }

    @Test
    void shouldThrowWhenNoStockInWalletForSell() {
        assertThrows(InsufficientStockException.class, () ->
                transactionService.executeTrade(WALLET_ID, STOCK_NAME, new TradeRequest("sell"))
        );
    }

    @Test
    void shouldAutoCreateWalletOnFirstBuy() {
        String newWallet = "auto-created-wallet";
        transactionService.executeTrade(newWallet, STOCK_NAME, new TradeRequest("buy"));

        assertEquals(1, getWalletQty(newWallet, STOCK_NAME));
    }

    // Helper methods
    private long getWalletQty(String walletId, String stockName) {
        return stockRepository.findByNameAndWalletId(stockName, walletId)
                .map(Stock::getQuantity)
                .orElse(0);
    }

    private int getBankQty(String stockName) {
        return stockRepository.findByNameAndWalletIsNull(stockName)
                .map(Stock::getQuantity)
                .orElse(0);
    }

    @Test
    void shouldHandleConcurrentBuysSafely() throws InterruptedException {
        int threadCount = 20;
        int initialBankStock = 10;
        AtomicInteger counter = new AtomicInteger(0);
        String walletId = "test-wallet";

        var bank = stockRepository.findByNameAndWalletIsNull(STOCK_NAME).get();
        bank.setQuantity(initialBankStock);
        stockRepository.save(bank);

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    walletController.executeTrade(walletId, STOCK_NAME, new TradeRequest("buy"));
                } catch (InsufficientStockException e) {
                    counter.incrementAndGet();
                } catch (Exception _) {
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        int finalBank = stockRepository.findByNameAndWalletIsNull(STOCK_NAME).get().getQuantity();
        assertEquals(0, finalBank);
        assertEquals(threadCount - initialBankStock, counter.get());
        assertEquals(initialBankStock, walletController.getStockQuantity(walletId, STOCK_NAME));
    }
}