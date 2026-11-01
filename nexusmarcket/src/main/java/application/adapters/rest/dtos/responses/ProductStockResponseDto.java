package application.adapters.rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockResponseDto {
    private Long id;
    private String productId;
    private String warehouseId;
    private String variantId;
    private int availableQuantity;
    private int reservedQuantity;
    private int sellableQuantity;
    private LocalDateTime lastUpdatedAt;
}
