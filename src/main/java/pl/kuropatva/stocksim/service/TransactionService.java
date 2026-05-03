package pl.kuropatva.stocksim.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
import pl.kuropatva.stocksim.service.trade.BuyOperation;
import pl.kuropatva.stocksim.service.trade.SellOperation;

@Service
public class TransactionService {

    private static final int TRADE_QTY = 1;
    private final WalletRepository walletRepo;
    private final AuditLogRepository logRepo;
    private final StockRepository stockRepo;

    public TransactionService(WalletRepository walletRepo, AuditLogRepository logRepo, StockRepository stockRepo) {
        this.walletRepo = walletRepo;
        this.logRepo = logRepo;
        this.stockRepo = stockRepo;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(maxRetries = 2, excludes = {InsufficientStockException.class, StockNotFoundException.class})
    public void executeTrade(String walletId, String stockName, TradeRequest tradeRequest) {
        String type = tradeRequest.type().toLowerCase();
        Wallet wallet = getWallet(walletId);
        switch (type) {
            case "buy" -> new BuyOperation().execute(wallet, stockName, stockRepo, TRADE_QTY);
            case "sell" -> new SellOperation().execute(wallet, stockName, stockRepo, TRADE_QTY);
            default -> {
                return;
            }
        }
        saveLogEntry(walletId, stockName, type);
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
