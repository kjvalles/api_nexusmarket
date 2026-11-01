package application.adapters.persistence.mongodb.mappers;

import application.adapters.persistence.mongodb.documents.ProductDocument;
import application.adapters.persistence.mongodb.documents.ProductVariantDocument;
import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;

import java.util.ArrayList;
import java.util.List;

public final class ProductMongoMapper {

    private ProductMongoMapper() {}

    public static ProductDocument toDocument(Product domain) {
        if (domain == null) {
            return null;
        }
        ProductDocument doc = new ProductDocument();
        doc.setId(domain.getId());
        doc.setVendorId(domain.getVendorId());
        doc.setName(domain.getName());
        doc.setDescription(domain.getDescription());
        doc.setCategory(domain.getCategory());
        doc.setBasePrice(domain.getBasePrice());
        doc.setType(domain.getType() != null ? domain.getType().getCode() : ProductType.PHYSICAL.getCode());
        doc.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : ProductStatus.SUSPENDED.getCode());
        doc.setImageUrls(domain.getImageUrls() != null ? new ArrayList<>(domain.getImageUrls()) : new ArrayList<>());
        doc.setAverageRating(domain.getAverageRating());
        doc.setTotalSales(domain.getTotalSales());
        doc.setPublishedAt(domain.getPublishedAt());
        doc.setUpdatedAt(domain.getUpdatedAt());

        List<ProductVariantDocument> variantDocs = new ArrayList<>();
        if (domain.getVariants() != null) {
            for (ProductVariant v : domain.getVariants()) {
                variantDocs.add(new ProductVariantDocument(
                        v.getVariantId(),
                        v.getColor(),
                        v.getSize(),
                        v.getSku(),
                        v.getAdditionalPrice(),
                        v.getImageUrl(),
                        v.isActive()
                ));
            }
        }
        doc.setVariants(variantDocs);
        return doc;
    }

    public static Product toDomain(ProductDocument doc) {
        if (doc == null) {
            return null;
        }
        Product domain = new Product();
        domain.setId(doc.getId());
        domain.setVendorId(doc.getVendorId());
        domain.setName(doc.getName());
        domain.setDescription(doc.getDescription());
        domain.setCategory(doc.getCategory());
        domain.setBasePrice(doc.getBasePrice());
        domain.setType(productType(doc.getType()));
        domain.setStatus(productStatus(doc.getStatus()));
        domain.setImageUrls(doc.getImageUrls() != null ? new ArrayList<>(doc.getImageUrls()) : new ArrayList<>());
        domain.setAverageRating(doc.getAverageRating());
        domain.setTotalSales(doc.getTotalSales());
        domain.setPublishedAt(doc.getPublishedAt());
        domain.setUpdatedAt(doc.getUpdatedAt());

        List<ProductVariant> variants = new ArrayList<>();
        if (doc.getVariants() != null) {
            for (ProductVariantDocument vDoc : doc.getVariants()) {
                variants.add(new ProductVariant(
                        vDoc.getVariantId(),
                        vDoc.getColor(),
                        vDoc.getSize(),
                        vDoc.getSku(),
                        vDoc.getAdditionalPrice(),
                        vDoc.getImageUrl(),
                        vDoc.isActive()
                ));
            }
        }
        domain.setVariants(variants);
        return domain;
    }

    public static ProductType productType(String code) {
        if (code == null) return null;
        if (ProductType.PHYSICAL.getCode().equalsIgnoreCase(code)) return ProductType.PHYSICAL;
        if (ProductType.DIGITAL.getCode().equalsIgnoreCase(code)) return ProductType.DIGITAL;
        return null;
    }

    public static ProductStatus productStatus(String code) {
        if (code == null) return null;
        if (ProductStatus.PUBLISHED.getCode().equalsIgnoreCase(code)) return ProductStatus.PUBLISHED;
        if (ProductStatus.SUSPENDED.getCode().equalsIgnoreCase(code)) return ProductStatus.SUSPENDED;
        if (ProductStatus.DISCONTINUED.getCode().equalsIgnoreCase(code)) return ProductStatus.DISCONTINUED;
        return null;
    }
}
