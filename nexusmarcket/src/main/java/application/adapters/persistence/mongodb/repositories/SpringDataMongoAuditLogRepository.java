package application.adapters.persistence.mongodb.repositories;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMongoAuditLogRepository extends MongoRepository<AuditLogDocument, String> {

    List<AuditLogDocument> findByActorId(String actorId);

    List<AuditLogDocument> findByEntityNameAndEntityId(String entityName, String entityId);
}
