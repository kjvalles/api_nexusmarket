package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.CreateProductRequestDto;
import application.adapters.rest.dtos.requests.CreateProductVariantRequestDto;
import application.adapters.rest.dtos.requests.UpdateProductRequestDto;
import application.adapters.rest.dtos.responses.ProductResponseDto;
import application.adapters.rest.dtos.responses.ProductStockResponseDto;
import application.adapters.rest.dtos.responses.ProductVariantResponseDto;
import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductVariant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SellerRestMapper {

    private SellerRestMapper() {}

    public static Product toDomain(CreateProductRequestDto dto, String vendorId) {
        if (dto == null) return null;
        ProductType type = resolveProductType(dto.getProductType());
        Product product = new Product(vendorId, dto.getName(), dto.getCategory(), dto.getBasePrice(), type);
        product.setDescription(dto.getDescription());
        if (dto.getImageUrls() != null) {
            product.setImageUrls(new ArrayList<>(dto.getImageUrls()));
        }

        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
            for (CreateProductVariantRequestDto vDto : dto.getVariants()) {
                String variantId = (vDto.getVariantId() != null && !vDto.getVariantId().isBlank())
                        ? vDto.getVariantId()
                        : UUID.randomUUID().toString();
                ProductVariant variant = new ProductVariant(
                        variantId,
                        vDto.getColor(),
                        vDto.getSize(),
                        vDto.getSku(),
                        vDto.getAdditionalPrice(),
                        vDto.getImageUrl(),
                        true
                );
                product.addVariant(variant);
            }
        }
        return product;
    }

    public static Product toDomain(UpdateProductRequestDto dto, String productId, String vendorId) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(productId);
        product.setVendorId(vendorId);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setBasePrice(dto.getBasePrice());
        if (dto.getImageUrls() != null) {
            product.setImageUrls(new ArrayList<>(dto.getImageUrls()));
        }
        return product;
    }

    public static ProductVariantResponseDto toVariantResponseDto(ProductVariant variant) {
        if (variant == null) return null;
        return ProductVariantResponseDto.builder()
                .variantId(variant.getVariantId())
                .color(variant.getColor())
                .size(variant.getSize())
                .sku(variant.getSku())
                .additionalPrice(variant.getAdditionalPrice())
                .imageUrl(variant.getImageUrl())
                .active(variant.isActive())
                .build();
    }

    public static ProductResponseDto toResponseDto(Product product) {
        if (product == null) return null;

        List<ProductVariantResponseDto> variantDtos = Collections.emptyList();
        if (product.getVariants() != null) {
            variantDtos = product.getVariants().stream()
                    .map(SellerRestMapper::toVariantResponseDto)
                    .collect(Collectors.toList());
        }

        return ProductResponseDto.builder()
                .id(product.getId())
                .vendorId(product.getVendorId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .basePrice(product.getBasePrice())
                .type(product.getType() != null ? product.getType().getCode() : null)
                .status(product.getStatus() != null ? product.getStatus().getCode() : null)
                .variants(variantDtos)
                .imageUrls(product.getImageUrls())
                .averageRating(product.getAverageRating())
                .totalSales(product.getTotalSales())
                .publishedAt(product.getPublishedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static List<ProductResponseDto> toResponseDtoList(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream().map(SellerRestMapper::toResponseDto).collect(Collectors.toList());
    }

    public static ProductStockResponseDto toResponseDto(ProductStock stock) {
        if (stock == null) return null;
        return ProductStockResponseDto.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .warehouseId(stock.getWarehouseId())
                .variantId(stock.getVariantId())
                .availableQuantity(stock.getAvailableQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .sellableQuantity(stock.getSellableQuantity())
                .lastUpdatedAt(stock.getLastUpdatedAt())
                .build();
    }

    public static List<ProductStockResponseDto> toStockResponseDtoList(List<ProductStock> stocks) {
        if (stocks == null) return Collections.emptyList();
        return stocks.stream().map(SellerRestMapper::toResponseDto).collect(Collectors.toList());
    }

    private static ProductType resolveProductType(String code) {
        if (code != null && "DIGITAL".equalsIgnoreCase(code)) {
            return ProductType.DIGITAL;
        }
        return ProductType.PHYSICAL;
    }
}
