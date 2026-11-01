package application.adapters.rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponseDto {
    private String id;
    private String name;
    private String vendorId;
    private String type;
    private String status;
    private String description;
    private int maxCapacity;
    private AddressDto address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
