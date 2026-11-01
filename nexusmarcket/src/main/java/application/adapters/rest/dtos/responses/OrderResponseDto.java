package application.adapters.rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String buyerId;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal taxes;
    private BigDecimal discounts;
    private BigDecimal totalAmount;
    private String additionalNotes;
    private List<OrderLineResponseDto> lines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
