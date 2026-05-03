package pl.kuropatva.stocksim.service.trade;

import pl.kuropatva.stocksim.exception.InsufficientStockException;
import pl.kuropatva.stocksim.model.entity.Stock;
import pl.kuropatva.stocksim.model.entity.Wallet;
import pl.kuropatva.stocksim.repository.StockRepository;

public class SellOperation implements TradeOperation {

    @Override
    public void execute(Wallet wallet, String stockName, StockRepository stockRepo, int TRADE_QTY) {
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
}
