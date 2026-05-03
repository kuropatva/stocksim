package pl.kuropatva.stocksim.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kuropatva.stocksim.exception.InsufficientStockException;
import pl.kuropatva.stocksim.exception.StockNotFoundException;
import pl.kuropatva.stocksim.model.dto.web.TradeRequest;
import pl.kuropatva.stocksim.model.entity.AuditLogEntry;
import pl.kuropatva.stocksim.model.entity.Stock;
import pl.kuropatva.stocksim.model.entity.Wallet;
import pl.kuropatva.stocksim.repository.AuditLogRepository;
import pl.kuropatva.stocksim.repository.StockRepository;
import pl.kuropatva.stocksim.repository.WalletRepository;

@Service
public class TransactionService {

    private final WalletRepository walletRepo;
    private final AuditLogRepository logRepo;
    private final StockRepository stockRepo;

    private static final int TRADE_QTY = 1;

    public TransactionService(WalletRepository walletRepo, AuditLogRepository logRepo, StockRepository stockRepo) {
        this.walletRepo = walletRepo;
        this.logRepo = logRepo;
        this.stockRepo = stockRepo;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(maxRetries = 2)
    public void executeTrade(String walletId, String stockName, TradeRequest tradeRequest) {
        String type = tradeRequest.type().toLowerCase();
        Wallet wallet = getWallet(walletId);

        switch (type) {
            case "buy" -> buy(wallet, stockName);
            case "sell" -> sell(wallet, stockName);
            default -> {
                return;
            }
        }

        saveLogEntry(walletId, stockName, type);
    }

    private void buy(Wallet wallet, String stockName) {
        Stock bankStock = stockRepo.findByNameAndWalletIsNullForUpdate(stockName).orElseThrow(() -> new StockNotFoundException("No stock with name " + stockName + " was found"));

        if (bankStock.getQuantity() < TRADE_QTY) {
            throw new InsufficientStockException("No stock in bank");
        }
        Stock walletStock = stockRepo.findByNameAndWalletIdForUpdate(stockName, wallet.getId()).orElseGet(() -> {
            Stock newStock = new Stock(stockName);
            newStock.setWallet(wallet);
            newStock.setQuantity(0);
            return newStock;
        });

        bankStock.modifyQuantity(-TRADE_QTY);
        walletStock.modifyQuantity(TRADE_QTY);
        stockRepo.save(bankStock);
        stockRepo.save(walletStock);
    }

    private void sell(Wallet wallet, String stockName) {
        Stock walletStock = stockRepo.findByNameAndWalletIdForUpdate(stockName, wallet.getId()).orElseThrow(() -> new InsufficientStockException("No stock in wallet"));

        if (walletStock.getQuantity() < TRADE_QTY) {
            throw new InsufficientStockException("No stock in wallet");
        }
        Stock bankStock = stockRepo.findByNameAndWalletIsNullForUpdate(stockName).orElseGet(() -> {
            Stock newBankStock = new Stock(stockName);
            newBankStock.setWallet(null);
            newBankStock.setQuantity(0);
            return newBankStock;
        });

        walletStock.modifyQuantity(-TRADE_QTY);
        bankStock.modifyQuantity(TRADE_QTY);
        stockRepo.save(bankStock);
        stockRepo.save(walletStock);
    }

    private void saveLogEntry(String walletId, String stockName, String type) {
        logRepo.save(new AuditLogEntry(type, walletId, stockName));
    }

    private Wallet getWallet(String walletId) {
        try {
            return walletRepo.findById(walletId).orElseGet(() -> walletRepo.saveAndFlush(new Wallet(walletId)));
        } catch (DataIntegrityViolationException e) {
            return walletRepo.findById(walletId).orElseThrow(() -> new RuntimeException("Failed to create wallet due to concurrent creation", e));
        }
    }
}
