package application.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class TaxBreakdown {

    private BigDecimal subtotal;
    private BigDecimal taxableBase;
    private BigDecimal vatPercentage;
    private BigDecimal vatAmount;
    private BigDecimal discounts;
    private BigDecimal total;
}
