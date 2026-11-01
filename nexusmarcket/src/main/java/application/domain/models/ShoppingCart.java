package application.domain.models;

import application.domain.valueobjects.CartItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidad de dominio que representa el Carrito de Compras de un comprador.
 */
@Getter
@Setter
public class ShoppingCart {

    private String id;
    private String buyerId;
    private List<CartItem> items = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public ShoppingCart() {
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = LocalDateTime.now();
    }

    public ShoppingCart(String buyerId) {
        this();
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("El identificador del comprador no puede ser nulo o vacío.");
        }
        this.buyerId = buyerId;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items != null ? items : Collections.emptyList());
    }

    public void addItem(CartItem item) {
        if (item == null) {
            throw new IllegalArgumentException("El ítem a añadir no puede ser nulo.");
        }
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        int existingIndex = -1;
        for (int i = 0; i < this.items.size(); i++) {
            if (this.items.get(i).equals(item)) {
                existingIndex = i;
                break;
            }
        }
        if (existingIndex >= 0) {
            CartItem current = this.items.get(existingIndex);
            this.items.set(existingIndex, current.withQuantity(current.getQuantity() + item.getQuantity()));
        } else {
            this.items.add(item);
        }
        this.lastModifiedAt = LocalDateTime.now();
    }

    public void removeItem(String productId, String variantId) {
        if (this.items != null) {
            this.items.removeIf(item ->
                    item.getProductId().equals(productId) &&
                    (variantId == null || variantId.equals(item.getVariantId())));
            this.lastModifiedAt = LocalDateTime.now();
        }
    }

    public void clear() {
        if (this.items != null) {
            this.items.clear();
        }
        this.lastModifiedAt = LocalDateTime.now();
    }

    public BigDecimal calculateTotal() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
