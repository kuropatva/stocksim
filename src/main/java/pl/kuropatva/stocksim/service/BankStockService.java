package pl.kuropatva.stocksim.service;


import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import pl.kuropatva.stocksim.mapper.StockMapper;
import pl.kuropatva.stocksim.model.dto.web.StockListDto;
import pl.kuropatva.stocksim.repository.StockRepository;

@Service
public class BankStockService {

    private StockRepository stockRepository;
    private StockMapper stockMapper;

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
        stockRepository.saveAll(stockMapper.toEntityList(stocks));
    }
}
