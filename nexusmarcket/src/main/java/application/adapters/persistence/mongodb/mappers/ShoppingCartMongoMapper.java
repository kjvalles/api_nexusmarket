package application.adapters.persistence.mongodb.mappers;

import application.adapters.persistence.mongodb.documents.CartItemDocument;
import application.adapters.persistence.mongodb.documents.ShoppingCartDocument;
import application.domain.models.ShoppingCart;
import application.domain.valueobjects.CartItem;

import java.util.ArrayList;
import java.util.List;

public final class ShoppingCartMongoMapper {

    private ShoppingCartMongoMapper() {}

    public static ShoppingCartDocument toDocument(ShoppingCart domain) {
        if (domain == null) {
            return null;
        }
        ShoppingCartDocument doc = new ShoppingCartDocument();
        doc.setId(domain.getId());
        doc.setBuyerId(domain.getBuyerId());
        doc.setCreatedAt(domain.getCreatedAt());
        doc.setLastModifiedAt(domain.getLastModifiedAt());

        List<CartItemDocument> itemDocs = new ArrayList<>();
        if (domain.getItems() != null) {
            for (CartItem item : domain.getItems()) {
                itemDocs.add(new CartItemDocument(
                        item.getProductId(),
                        item.getVariantId(),
                        item.getProductName(),
                        item.getVariantSku(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getAddedAt()
                ));
            }
        }
        doc.setItems(itemDocs);
        return doc;
    }

    public static ShoppingCart toDomain(ShoppingCartDocument doc) {
        if (doc == null) {
            return null;
        }
        ShoppingCart domain = new ShoppingCart();
        domain.setId(doc.getId());
        domain.setBuyerId(doc.getBuyerId());
        domain.setCreatedAt(doc.getCreatedAt());
        domain.setLastModifiedAt(doc.getLastModifiedAt());

        if (doc.getItems() != null) {
            for (CartItemDocument itemDoc : doc.getItems()) {
                domain.addItem(new CartItem(
                        itemDoc.getProductId(),
                        itemDoc.getVariantId(),
                        itemDoc.getProductName(),
                        itemDoc.getVariantSku(),
                        itemDoc.getQuantity(),
                        itemDoc.getUnitPrice(),
                        itemDoc.getAddedAt()
                ));
            }
        }
        return domain;
    }
}
