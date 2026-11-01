package application.adapters.rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineResponseDto {
    private String productId;
    private String variantId;
    private String productName;
    private String variantSku;
    private String productTypeCode;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
