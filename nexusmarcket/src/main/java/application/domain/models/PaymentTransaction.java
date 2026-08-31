package application.domain.models;

import application.domain.valueobjects.PaymentMethod;
import application.domain.valueobjects.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PaymentTransaction {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String authorizationCode;
    private String paymentReference;
    private String validationResult;
    private LocalDateTime transactionDate;
    private LocalDateTime updatedAt;
}
