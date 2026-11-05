package application.domain.services.order;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.OrderLine;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de dominio para creación y cálculo de nuevos pedidos.
 */
@RequiredArgsConstructor
public class CreateOrderService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public Order createOrder(String buyerId, List<OrderLine> lines, String additionalNotes) {
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("El comprador es obligatorio.");
        }
        if (lines == null || lines.isEmpty()) {
            throw new DomainException("El pedido debe contener al menos un producto.");
        }

        Order order = new Order(buyerId);
        order.setAdditionalNotes(additionalNotes);

        for (OrderLine line : lines) {
            Product product = productRepositoryPort.findById(line.getProductId())
                    .orElseThrow(() -> new ProductNotAvailableException("Producto no encontrado: " + line.getProductId()));

            if (!product.isPublished()) {
                throw new ProductNotAvailableException("El producto '" + product.getName() + "' no se encuentra disponible para la venta.");
            }

            // Recalcular y validar el precio oficial vigente desde el catálogo (fuente de verdad)
            BigDecimal currentPrice = product.getBasePrice();
            if (line.getVariantId() != null && !line.getVariantId().isBlank()) {
                currentPrice = product.getVariants().stream()
                        .filter(v -> v.getVariantId().equals(line.getVariantId()))
                        .findFirst()
                        .map(v -> product.getBasePrice().add(v.getAdditionalPrice() != null ? v.getAdditionalPrice() : BigDecimal.ZERO))
                        .orElse(product.getBasePrice());
            }

            // Congelar la línea de pedido con los datos y el precio oficial vigente del catálogo
            OrderLine verifiedLine = new OrderLine(
                    line.getProductId(),
                    line.getVariantId(),
                    product.getName(),
                    currentPrice,
                    line.getQuantity()
            );

            order.addLine(verifiedLine);
        }

        return orderRepositoryPort.save(order);
    }
}
