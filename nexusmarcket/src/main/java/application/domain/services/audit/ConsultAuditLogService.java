package application.domain.services.audit;

import application.domain.models.AuditLog;
import application.domain.ports.out.AuditLogRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Servicio de dominio para consulta de bitácoras inmutables de auditoría.
 */
@RequiredArgsConstructor
public class ConsultAuditLogService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    public List<AuditLog> getAuditLogs(String resourceFilter) {
        if (resourceFilter != null && !resourceFilter.isBlank()) {
            return auditLogRepositoryPort.findByEntity(resourceFilter, null);
        }
        return auditLogRepositoryPort.findAll();
    }
}
