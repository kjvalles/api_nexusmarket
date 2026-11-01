package application.adapters.persistence.mongodb.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Documento MongoDB para catálogo de productos (colección products).
 */
@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductDocument {

    @Id
    private String id;

    private String vendorId;
    private String name;
    private String description;
    private String category;
    private BigDecimal basePrice;
    private String type;
    private String status;
    private List<ProductVariantDocument> variants = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private double averageRating;
    private int totalSales;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
