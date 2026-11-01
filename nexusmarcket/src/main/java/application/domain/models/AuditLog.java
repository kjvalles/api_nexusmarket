package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Modelo de dominio que representa un registro inmutable de auditoría para operaciones críticas.
 */
@Getter
@Setter
public class AuditLog {

    private String id;
    private String actorId;
    private String actorRole;
    private String action;
    private String entityName;
    private String entityId;
    private String details;
    private LocalDateTime timestamp;

    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(String actorId, String actorRole, String action, String entityName, String entityId, String details) {
        this();
        this.actorId = actorId;
        this.actorRole = actorRole;
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.details = details;
    }
}
