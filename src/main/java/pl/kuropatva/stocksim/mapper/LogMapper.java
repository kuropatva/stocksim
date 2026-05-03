package pl.kuropatva.stocksim.mapper;


import org.springframework.stereotype.Component;
import pl.kuropatva.stocksim.model.dto.web.LogDto;
import pl.kuropatva.stocksim.model.dto.web.LogListDto;
import pl.kuropatva.stocksim.model.entity.AuditLogEntry;

import java.util.List;

@Component
public class LogMapper {

    private LogDto toLogDto(AuditLogEntry logEntry) {
        return new LogDto(logEntry.getType(), logEntry.getWalletId(), logEntry.getStockName());
    }

    public LogListDto toList(List<AuditLogEntry> all) {
        return new LogListDto(all.stream().map(this::toLogDto).toList());
    }
}
