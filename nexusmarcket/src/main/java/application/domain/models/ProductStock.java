package application.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProductStock {

    private Long id;
    private String productId;
    private String warehouseId;
    private String variantId;
    private int availableQuantity;
    private int reservedQuantity;
    private int sellableQuantity;
    private LocalDateTime lastUpdatedAt;
}
