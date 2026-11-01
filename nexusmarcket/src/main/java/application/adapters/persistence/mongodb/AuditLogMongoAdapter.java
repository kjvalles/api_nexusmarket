package application.adapters.persistence.mongodb;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import application.adapters.persistence.mongodb.mappers.AuditLogMongoMapper;
import application.adapters.persistence.mongodb.repositories.SpringDataMongoAuditLogRepository;
import application.domain.models.AuditLog;
import application.domain.ports.out.AuditLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adaptador de salida para persistencia de Registros de Auditoría (MongoDB).
 */
@Repository
@RequiredArgsConstructor
public class AuditLogMongoAdapter implements AuditLogRepositoryPort {

    private final SpringDataMongoAuditLogRepository auditLogRepository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        AuditLogDocument doc = AuditLogMongoMapper.toDocument(auditLog);
        AuditLogDocument saved = auditLogRepository.save(doc);
        return AuditLogMongoMapper.toDomain(saved);
    }

    @Override
    public List<AuditLog> findByActorId(String actorId) {
        if (actorId == null || actorId.isBlank()) {
            return List.of();
        }
        return auditLogRepository.findByActorId(actorId).stream()
                .map(AuditLogMongoMapper::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findByEntity(String entityName, String entityId) {
        if (entityName == null || entityId == null) {
            return List.of();
        }
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId).stream()
                .map(AuditLogMongoMapper::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll().stream()
                .map(AuditLogMongoMapper::toDomain)
                .toList();
    }
}
