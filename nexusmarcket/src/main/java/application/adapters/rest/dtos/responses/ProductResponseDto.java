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
public class ProductResponseDto {
    private String id;
    private String vendorId;
    private String name;
    private String description;
    private String category;
    private BigDecimal basePrice;
    private String type;
    private String status;
    private List<ProductVariantResponseDto> variants;
    private List<String> imageUrls;
    private double averageRating;
    private int totalSales;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
