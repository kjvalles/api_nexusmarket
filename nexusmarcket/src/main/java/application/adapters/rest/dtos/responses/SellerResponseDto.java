package application.adapters.rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponseDto {
    private String id;
    private String identityDocument;
    private String fullName;
    private String email;
    private String role;
    private String status;
    private String businessName;
    private String taxId;
    private String sellerStatus;
    private String businessDescription;
    private double rating;
    private LocalDateTime incorporationDate;
    private int activeProductsCount;
    private List<String> warehouseIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
