package application.domain.models;

import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.RefundStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Refund {

    private Long id;
    private Long paymentTransactionId;
    private Long returnId;
    private BigDecimal refundedAmount;
    private RefundStatus status;
    private PaymentMethod paymentMethod;
    private String reason;
    private LocalDateTime requestedAt;
    private LocalDateTime executedAt;
    private LocalDateTime updatedAt;
}
