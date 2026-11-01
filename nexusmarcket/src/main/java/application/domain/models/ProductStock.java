package application.domain.models;

import application.domain.exceptions.NegativeStockException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa el balance contable de inventario por producto, variante y bodega.
 * Garantiza la regla de negocio crítica: jamás permitir existencias negativas.
 */
@Getter
@Setter
public class ProductStock {

    private Long id;
    private String productId;
    private String warehouseId;
    private String variantId;
    private int availableQuantity = 0;
    private int reservedQuantity = 0;
    private LocalDateTime lastUpdatedAt;

    public ProductStock() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public ProductStock(String productId, String variantId, String warehouseId, int initialStock) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El productId no puede ser nulo o vacío.");
        }
        if (warehouseId == null || warehouseId.isBlank()) {
            throw new IllegalArgumentException("El warehouseId no puede ser nulo o vacío.");
        }
        if (initialStock < 0) {
            throw new NegativeStockException("El stock inicial no puede ser negativo.");
        }
        this.productId = productId;
        this.variantId = variantId;
        this.warehouseId = warehouseId;
        this.availableQuantity = initialStock;
        this.reservedQuantity = 0;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Retorna el stock efectivamente vendible (disponible menos reservado).
     */
    public int getSellableQuantity() {
        return availableQuantity - reservedQuantity;
    }

    /**
     * Reserva unidades para un pedido en proceso de compra.
     */
    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a reservar debe ser mayor a cero.");
        }
        if (getSellableQuantity() < quantity) {
            throw new NegativeStockException(productId, warehouseId, quantity, getSellableQuantity());
        }
        this.reservedQuantity += quantity;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Libera una reserva previa por cancelación de orden.
     */
    public void releaseReservation(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a liberar debe ser mayor a cero.");
        }
        if (this.reservedQuantity < quantity) {
            this.reservedQuantity = 0;
        } else {
            this.reservedQuantity -= quantity;
        }
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Confirma la salida definitiva de inventario por venta despachada.
     */
    public void confirmSale(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad despachada debe ser mayor a cero.");
        }
        releaseReservation(quantity);
        if (this.availableQuantity < quantity) {
            throw new NegativeStockException(productId, warehouseId, quantity, this.availableQuantity);
        }
        this.availableQuantity -= quantity;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Ingreso de nuevo stock físico a bodega.
     */
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a ingresar debe ser mayor a cero.");
        }
        this.availableQuantity += quantity;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Ajuste manual de inventario (auditoría o merma).
     */
    public void adjustStock(int delta) {
        if (this.availableQuantity + delta < this.reservedQuantity) {
            throw new NegativeStockException(productId, warehouseId, delta, getSellableQuantity());
        }
        this.availableQuantity += delta;
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
