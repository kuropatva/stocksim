package pl.kuropatva.stocksim.service.trade;

import pl.kuropatva.stocksim.model.entity.Wallet;
import pl.kuropatva.stocksim.repository.StockRepository;

public interface TradeOperation {
    void execute(Wallet wallet, String stockName, StockRepository stockRepo, int TRADE_QTY);
}
