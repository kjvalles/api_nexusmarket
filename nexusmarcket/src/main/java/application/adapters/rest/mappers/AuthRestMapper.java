package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.RegisterBuyerRequestDto;
import application.adapters.rest.dtos.responses.AddressDto;
import application.adapters.rest.dtos.responses.AuthResponseDto;
import application.adapters.rest.dtos.responses.BuyerResponseDto;
import application.adapters.rest.dtos.responses.ProductResponseDto;
import application.adapters.rest.dtos.responses.ProductVariantResponseDto;
import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.ProductVariant;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class AuthRestMapper {

    private AuthRestMapper() {}

    public static Buyer toDomain(RegisterBuyerRequestDto dto) {
        if (dto == null) return null;
        Buyer buyer = new Buyer();
        buyer.setIdentityDocument(dto.getIdentityDocument());
        buyer.setFullName(dto.getFullName());
        buyer.setEmail(dto.getEmail());

        Address address = new Address(
                dto.getStreet(),
                dto.getCity(),
                dto.getState(),
                dto.getPostalCode(),
                dto.getCountry(),
                dto.getAdditionalInstructions()
        );
        buyer.setPrimaryAddress(address);
        return buyer;
    }

    public static BuyerResponseDto toResponseDto(Buyer buyer) {
        if (buyer == null) return null;

        AddressDto primary = null;
        if (buyer.getPrimaryAddress() != null) {
            primary = AddressDto.builder()
                    .street(buyer.getPrimaryAddress().getStreet())
                    .city(buyer.getPrimaryAddress().getCity())
                    .state(buyer.getPrimaryAddress().getState())
                    .postalCode(buyer.getPrimaryAddress().getPostalCode())
                    .country(buyer.getPrimaryAddress().getCountry())
                    .additionalInstructions(buyer.getPrimaryAddress().getAdditionalInstructions())
                    .build();
        }

        List<AddressDto> additionals = Collections.emptyList();
        if (buyer.getAdditionalAddresses() != null) {
            additionals = buyer.getAdditionalAddresses().stream()
                    .map(a -> AddressDto.builder()
                            .street(a.getStreet())
                            .city(a.getCity())
                            .state(a.getState())
                            .postalCode(a.getPostalCode())
                            .country(a.getCountry())
                            .additionalInstructions(a.getAdditionalInstructions())
                            .build())
                    .collect(Collectors.toList());
        }

        return BuyerResponseDto.builder()
                .id(buyer.getId())
                .identityDocument(buyer.getIdentityDocument())
                .fullName(buyer.getFullName())
                .email(buyer.getEmail())
                .role(buyer.getRole() != null ? buyer.getRole().name() : null)
                .status(buyer.getStatus() != null ? buyer.getStatus().getCode() : null)
                .commercialStatus(buyer.getCommercialStatus() != null ? buyer.getCommercialStatus().getCode() : null)
                .virtualBalance(buyer.getVirtualBalance())
                .completedOrdersCount(buyer.getCompletedOrdersCount())
                .rating(buyer.getRating())
                .primaryAddress(primary)
                .additionalAddresses(additionals)
                .createdAt(buyer.getCreatedAt())
                .updatedAt(buyer.getUpdatedAt())
                .build();
    }

    public static AuthResponseDto toAuthResponseDto(String token, User user, long expiresInSeconds) {
        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user != null ? user.getId() : null)
                .email(user != null ? user.getEmail() : null)
                .role(user != null && user.getRole() != null ? user.getRole().name() : null)
                .expiresInSeconds(expiresInSeconds)
                .build();
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

    public static ProductResponseDto toProductResponseDto(Product product) {
        if (product == null) return null;

        List<ProductVariantResponseDto> variantDtos = Collections.emptyList();
        if (product.getVariants() != null) {
            variantDtos = product.getVariants().stream()
                    .map(AuthRestMapper::toVariantResponseDto)
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

    public static List<ProductResponseDto> toProductResponseDtoList(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream().map(AuthRestMapper::toProductResponseDto).collect(Collectors.toList());
    }
}
