package application.domain.ports.out;

import application.domain.models.AuditLog;

import java.util.List;

/**
 * Puerto de salida para persistencia de registros de auditoría (MongoDB).
 */
public interface AuditLogRepositoryPort {

    AuditLog save(AuditLog auditLog);

    List<AuditLog> findByActorId(String actorId);

    List<AuditLog> findByEntity(String entityName, String entityId);

    List<AuditLog> findAll();
}
