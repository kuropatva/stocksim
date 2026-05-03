package pl.kuropatva.stocksim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kuropatva.stocksim.model.entity.AuditLogEntry;

public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Integer> {
}
