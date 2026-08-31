package application.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class OrderLine {

    private String productId;
    private String variantId;
    private String productName;
    private String variantSku;
    private String productTypeCode;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
