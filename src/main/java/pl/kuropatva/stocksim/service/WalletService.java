package pl.kuropatva.stocksim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kuropatva.stocksim.mapper.StockMapper;
import pl.kuropatva.stocksim.model.dto.web.WalletDto;
import pl.kuropatva.stocksim.repository.StockRepository;
import pl.kuropatva.stocksim.repository.WalletRepository;

@Service
public class WalletService {

    private WalletRepository walletRepository;
    private StockRepository stockRepository;
    private StockMapper stockMapper;

    public WalletService(WalletRepository walletRepository, StockRepository stockRepository, StockMapper stockMapper) {
        this.walletRepository = walletRepository;
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
    }

    public WalletDto getAllStocks(String walletId) {
        var wallet = walletRepository.findById(walletId);
        if (wallet.isEmpty()) return new WalletDto(walletId, null);
        return new WalletDto(walletId, wallet.get().getStocks().stream().map(stockMapper::toDto).toList());
    }

    public long getStockQuantity(String walletId, String stockName) {
        var stock = stockRepository.findByNameAndWalletId(stockName, walletId);
        if (stock.isEmpty()) return 0;
        return stock.get().getQuantity();
    }
}
