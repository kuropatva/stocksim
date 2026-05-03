package pl.kuropatva.stocksim.service;

import org.springframework.stereotype.Service;
import pl.kuropatva.stocksim.mapper.LogMapper;
import pl.kuropatva.stocksim.model.dto.web.LogListDto;
import pl.kuropatva.stocksim.repository.AuditLogRepository;

@Service
public class LogService {

    private final AuditLogRepository auditLogRepository;
    private final LogMapper logMapper;

    public LogService(AuditLogRepository auditLogRepository, LogMapper logMapper) {
        this.auditLogRepository = auditLogRepository;
        this.logMapper = logMapper;
    }

    public LogListDto getLogListDto() {
        return logMapper.toList(auditLogRepository.findAll());
    }

    public void deleteLog() {
        auditLogRepository.deleteAll();
    }
}
