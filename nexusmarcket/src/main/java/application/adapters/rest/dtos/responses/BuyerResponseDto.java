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
public class BuyerResponseDto {
    private String id;
    private String identityDocument;
    private String fullName;
    private String email;
    private String role;
    private String status;
    private String commercialStatus;
    private BigDecimal virtualBalance;
    private int completedOrdersCount;
    private double rating;
    private AddressDto primaryAddress;
    private List<AddressDto> additionalAddresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
