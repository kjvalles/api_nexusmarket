package application.adapters.persistence.mongodb.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Documento MongoDB para Carrito de Compras (colección shopping_carts).
 */
@Document(collection = "shopping_carts")
@Getter
@Setter
@NoArgsConstructor
public class ShoppingCartDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String buyerId;

    private List<CartItemDocument> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
