package application.adapters.persistence.mongodb.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Sub-documento embebido para ítems del carrito de compras en MongoDB.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDocument {

    private String productId;
    private String variantId;
    private String productName;
    private String variantSku;
    private int quantity;
    private BigDecimal unitPrice;
    private LocalDateTime addedAt;
}
