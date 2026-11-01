package application.adapters.persistence.mongodb.mappers;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import application.domain.models.AuditLog;

public final class AuditLogMongoMapper {

    private AuditLogMongoMapper() {}

    public static AuditLogDocument toDocument(AuditLog domain) {
        if (domain == null) {
            return null;
        }
        AuditLogDocument doc = new AuditLogDocument();
        doc.setId(domain.getId());
        doc.setActorId(domain.getActorId());
        doc.setActorRole(domain.getActorRole());
        doc.setAction(domain.getAction());
        doc.setEntityName(domain.getEntityName());
        doc.setEntityId(domain.getEntityId());
        doc.setDetails(domain.getDetails());
        doc.setTimestamp(domain.getTimestamp());
        return doc;
    }

    public static AuditLog toDomain(AuditLogDocument doc) {
        if (doc == null) {
            return null;
        }
        AuditLog domain = new AuditLog();
        domain.setId(doc.getId());
        domain.setActorId(doc.getActorId());
        domain.setActorRole(doc.getActorRole());
        domain.setAction(doc.getAction());
        domain.setEntityName(doc.getEntityName());
        domain.setEntityId(doc.getEntityId());
        domain.setDetails(doc.getDetails());
        domain.setTimestamp(doc.getTimestamp());
        return domain;
    }
}
