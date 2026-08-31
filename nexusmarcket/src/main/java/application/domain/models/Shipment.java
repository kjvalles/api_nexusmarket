package application.domain.models;

import application.domain.valueobjects.DeliveryAddress;
import application.domain.valueobjects.ShipmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Shipment {

    private Long id;
    private Long orderId;
    private DeliveryAddress deliveryAddress;
    private String sourceWarehouseId;
    private ShipmentStatus status;
    private LocalDate estimatedDeliveryDate;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;
    private String internalTrackingNumber;
    private String logisticsOperatorId;
    private String observations;
    private LocalDateTime createdAt;
}
