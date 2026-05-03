package pl.kuropatva.stocksim.mapper;

import org.springframework.stereotype.Component;
import pl.kuropatva.stocksim.model.dto.web.StockDto;
import pl.kuropatva.stocksim.model.dto.web.StockListDto;
import pl.kuropatva.stocksim.model.entity.Stock;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockMapper {

    public Stock toEntity(StockDto stockDto) {
        Stock stock = new Stock();
        stock.setName(stockDto.name());
        stock.setQuantity(stockDto.quantity());
        return stock;
    }

    public List<Stock> toEntityList(StockListDto request) {
        return request.stocks().stream()
                .map(this::toEntity)
                .toList();
    }

    public StockListDto toListDto(Collection<Stock> stocks) {
        return new StockListDto(stocks.stream().map(this::toDto).collect(Collectors.toList()));
    }

    public StockDto toDto(Stock stock) {
        return new StockDto(stock.getName(), stock.getQuantity());
    }
}