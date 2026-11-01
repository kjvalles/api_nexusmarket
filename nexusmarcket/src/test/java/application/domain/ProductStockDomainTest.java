package application.domain;

import application.domain.exceptions.NegativeStockException;
import application.domain.models.ProductStock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias del Agregado ProductStock (Invariante de Inventario No Negativo)")
class ProductStockDomainTest {

    @Test
    @DisplayName("Debe calcular correctamente la cantidad vendible (disponible - reservada)")
    void shouldCalculateSellableQuantity() {
        ProductStock stock = new ProductStock("prod-1", "var-1", "wh-1", 100);

        assertEquals(100, stock.getSellableQuantity());
        assertEquals(100, stock.getAvailableQuantity());
        assertEquals(0, stock.getReservedQuantity());

        stock.reserve(30);
        assertEquals(70, stock.getSellableQuantity());
        assertEquals(30, stock.getReservedQuantity());
        assertEquals(100, stock.getAvailableQuantity());
    }

    @Test
    @DisplayName("Debe lanzar NegativeStockException al intentar reservar más de lo vendible")
    void shouldThrowNegativeStockExceptionOnOverReservation() {
        ProductStock stock = new ProductStock("prod-1", null, "wh-1", 10);

        NegativeStockException ex = assertThrows(NegativeStockException.class, () -> stock.reserve(15));
        assertTrue(ex.getMessage().toLowerCase().contains("existencias insuficientes"));
    }

    @Test
    @DisplayName("Debe liberar reservas correctamente sin sobrepasar el total")
    void shouldReleaseReservationCorrectly() {
        ProductStock stock = new ProductStock("prod-1", null, "wh-1", 50);
        stock.reserve(20);
        assertEquals(30, stock.getSellableQuantity());

        stock.releaseReservation(10);
        assertEquals(40, stock.getSellableQuantity());
        assertEquals(10, stock.getReservedQuantity());
    }

    @Test
    @DisplayName("Debe descontar existencias físicas al confirmar venta despachada")
    void shouldConfirmSaleAndReduceAvailableQuantity() {
        ProductStock stock = new ProductStock("prod-1", null, "wh-1", 50);
        stock.reserve(15);

        stock.confirmSale(15);
        assertEquals(35, stock.getAvailableQuantity());
        assertEquals(0, stock.getReservedQuantity());
        assertEquals(35, stock.getSellableQuantity());
    }

    @Test
    @DisplayName("Debe rechazar ajustes que causen existencias por debajo de lo reservado")
    void shouldRejectAdjustmentsExceedingSellableStock() {
        ProductStock stock = new ProductStock("prod-1", null, "wh-1", 20);
        stock.reserve(15);

        // Vendible es 5. Ajustar -10 debe lanzar NegativeStockException
        assertThrows(NegativeStockException.class, () -> stock.adjustStock(-10));
    }
}
