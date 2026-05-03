package pl.kuropatva.stocksim.service.trade;

import pl.kuropatva.stocksim.exception.InsufficientStockException;
import pl.kuropatva.stocksim.exception.StockNotFoundException;
import pl.kuropatva.stocksim.model.entity.Stock;
import pl.kuropatva.stocksim.model.entity.Wallet;
import pl.kuropatva.stocksim.repository.StockRepository;

public class BuyOperation implements TradeOperation {

    @Override
    public void execute(Wallet wallet, String stockName, StockRepository stockRepo, int TRADE_QTY) {
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
}
