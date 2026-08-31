package application.domain.models;

import application.domain.valueobjects.OrderLine;
import application.domain.valueobjects.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Order {

    private Long id;
    private String buyerId;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal taxes;
    private BigDecimal discounts;
    private BigDecimal totalAmount;
    private String additionalNotes;
    private List<OrderLine> orderLines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
