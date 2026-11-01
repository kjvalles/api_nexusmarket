package application.domain.models;

import application.domain.exceptions.DomainException;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Raíz del Agregado de Catálogo de Productos (Product Aggregate Root).
 * Representa un producto físico o digital en el catálogo del Marketplace.
 */
@Getter
@Setter
public class Product {

    private String id;
    private String vendorId;
    private String name;
    private String description;
    private String category;
    private BigDecimal basePrice;
    private ProductType type;
    private ProductStatus status;
    private List<ProductVariant> variants = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private double averageRating = 0.0;
    private int totalSales = 0;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    public Product() {
        this.status = ProductStatus.SUSPENDED;
        this.type = ProductType.PHYSICAL;
        this.updatedAt = LocalDateTime.now();
    }

    public Product(String vendorId, String name, String category, BigDecimal basePrice, ProductType type) {
        if (vendorId == null || vendorId.isBlank()) {
            throw new IllegalArgumentException("El vendorId no puede ser nulo o vacío.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo o nulo.");
        }
        this.vendorId = vendorId;
        this.name = name;
        this.category = category != null ? category : "GENERAL";
        this.basePrice = basePrice;
        this.type = type != null ? type : ProductType.PHYSICAL;
        this.status = ProductStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public List<ProductVariant> getVariants() {
        return Collections.unmodifiableList(variants != null ? variants : Collections.emptyList());
    }

    public void publish() {
        if (ProductStatus.DISCONTINUED.equals(this.status)) {
            throw new DomainException("Un producto descontinuado no puede volver a publicarse.");
        }
        this.status = ProductStatus.PUBLISHED;
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        if (ProductStatus.DISCONTINUED.equals(this.status)) {
            throw new DomainException("Un producto descontinuado no puede suspenderse.");
        }
        this.status = ProductStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
        this.updatedAt = LocalDateTime.now();
    }

    public void addVariant(ProductVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("La variante no puede ser nula.");
        }
        if (this.variants == null) {
            this.variants = new ArrayList<>();
        }
        this.variants.add(variant);
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El nuevo precio base no puede ser negativo.");
        }
        this.basePrice = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordSale(int quantity) {
        if (quantity > 0) {
            this.totalSales += quantity;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isPublished() {
        return ProductStatus.PUBLISHED.equals(this.status);
    }
}
