package pl.kuropatva.stocksim.model.dto.web;

import java.util.List;

public record WalletDto(String id, List<StockDto> stocks) {
}
