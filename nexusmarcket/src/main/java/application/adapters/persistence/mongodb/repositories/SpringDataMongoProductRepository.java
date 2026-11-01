package application.adapters.persistence.mongodb.repositories;

import application.adapters.persistence.mongodb.documents.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMongoProductRepository extends MongoRepository<ProductDocument, String> {

    List<ProductDocument> findByVendorId(String vendorId);

    List<ProductDocument> findByStatus(String status);

    List<ProductDocument> findByStatusAndCategory(String status, String category);
}
