package application.adapters.persistence.mongodb.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Documento MongoDB para Bitácora Inmutable de Auditoría (colección audit_logs).
 */
@Document(collection = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogDocument {

    @Id
    private String id;

    private String actorId;
    private String actorRole;
    private String action;
    private String entityName;
    private String entityId;
    private String details;
    private LocalDateTime timestamp;
}
