package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private String id;
    private String vendorId;
    private String name;
    private String description;
    private String category;
    private BigDecimal basePrice;
    private ProductType type;
    private ProductStatus status;
    private List<ProductVariant> variants;
    private List<String> imageUrls;
    private double averageRating;
    private int totalSales;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
