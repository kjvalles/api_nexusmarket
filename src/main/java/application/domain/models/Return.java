package application.domain.models;

import application.domain.valueobjects.ReturnStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Return {

    private Long id;
    private Long orderId;
    private String productId;
    private String variantId;
    private int quantity;
    private String reason;
    private ReturnStatus status;
    private String receivedProductCondition;
    private Long refundId;
    private Long returnShipmentId;
    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;
}
