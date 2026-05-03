package pl.kuropatva.stocksim.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.kuropatva.stocksim.mapper.StockMapper;
import pl.kuropatva.stocksim.model.dto.web.StockListDto;
import pl.kuropatva.stocksim.repository.StockRepository;

@Service
public class BankStockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    public BankStockService(StockRepository stockRepository, StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
    }

    public StockListDto getAllStocks() {
        return stockMapper.toListDto(stockRepository.findAllByWalletId(null));
    }

    @Transactional
    public void setAllStocks(StockListDto stocks) {
        stockRepository.deleteAllByWalletId(null);
        stockRepository.saveAllAndFlush(stockMapper.toEntityList(stocks));
    }
}
