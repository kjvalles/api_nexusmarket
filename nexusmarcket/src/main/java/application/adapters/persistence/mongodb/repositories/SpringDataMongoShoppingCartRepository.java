package application.adapters.persistence.mongodb.repositories;

import application.adapters.persistence.mongodb.documents.ShoppingCartDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataMongoShoppingCartRepository extends MongoRepository<ShoppingCartDocument, String> {

    Optional<ShoppingCartDocument> findByBuyerId(String buyerId);

    void deleteByBuyerId(String buyerId);
}
