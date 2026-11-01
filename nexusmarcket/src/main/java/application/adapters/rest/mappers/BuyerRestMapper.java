package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.AddItemToCartRequestDto;
import application.adapters.rest.dtos.requests.CheckoutOrderRequestDto;
import application.adapters.rest.dtos.responses.CartItemResponseDto;
import application.adapters.rest.dtos.responses.OrderLineResponseDto;
import application.adapters.rest.dtos.responses.OrderResponseDto;
import application.adapters.rest.dtos.responses.ShoppingCartResponseDto;
import application.domain.models.Order;
import application.domain.models.ShoppingCart;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.CartItem;
import application.domain.valueobjects.OrderLine;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class BuyerRestMapper {

    private BuyerRestMapper() {}

    public static CartItem toDomain(AddItemToCartRequestDto dto) {
        if (dto == null) return null;
        return new CartItem(
                dto.getProductId(),
                dto.getVariantId(),
                dto.getProductName(),
                dto.getVariantSku(),
                dto.getQuantity(),
                dto.getUnitPrice(),
                LocalDateTime.now()
        );
    }

    public static Address toDomainAddress(CheckoutOrderRequestDto dto) {
        if (dto == null) return null;
        return new Address(
                dto.getStreet(),
                dto.getCity(),
                dto.getState(),
                dto.getPostalCode(),
                dto.getCountry(),
                dto.getAdditionalInstructions()
        );
    }

    public static CartItemResponseDto toCartItemResponseDto(CartItem item) {
        if (item == null) return null;
        return CartItemResponseDto.builder()
                .productId(item.getProductId())
                .variantId(item.getVariantId())
                .productName(item.getProductName())
                .variantSku(item.getVariantSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .addedAt(item.getAddedAt())
                .build();
    }

    public static ShoppingCartResponseDto toResponseDto(ShoppingCart cart) {
        if (cart == null) return null;
        List<CartItemResponseDto> itemDtos = Collections.emptyList();
        if (cart.getItems() != null) {
            itemDtos = cart.getItems().stream()
                    .map(BuyerRestMapper::toCartItemResponseDto)
                    .collect(Collectors.toList());
        }

        int totalItems = 0;
        if (cart.getItems() != null) {
            totalItems = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
        }

        return ShoppingCartResponseDto.builder()
                .id(cart.getId())
                .buyerId(cart.getBuyerId())
                .items(itemDtos)
                .totalItems(totalItems)
                .totalPrice(cart.calculateTotal())
                .createdAt(cart.getCreatedAt())
                .lastModifiedAt(cart.getLastModifiedAt())
                .build();
    }

    public static OrderLineResponseDto toOrderLineResponseDto(OrderLine line) {
        if (line == null) return null;
        return OrderLineResponseDto.builder()
                .productId(line.getProductId())
                .variantId(line.getVariantId())
                .productName(line.getProductName())
                .variantSku(line.getVariantSku())
                .productTypeCode(line.getProductTypeCode())
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .subtotal(line.getSubtotal())
                .build();
    }

    public static OrderResponseDto toResponseDto(Order order) {
        if (order == null) return null;
        List<OrderLineResponseDto> lineDtos = Collections.emptyList();
        if (order.getOrderLines() != null) {
            lineDtos = order.getOrderLines().stream()
                    .map(BuyerRestMapper::toOrderLineResponseDto)
                    .collect(Collectors.toList());
        }

        return OrderResponseDto.builder()
                .id(order.getId())
                .buyerId(order.getBuyerId())
                .status(order.getStatus() != null ? order.getStatus().getCode() : null)
                .subtotal(order.getSubtotal())
                .taxes(order.getTaxes())
                .discounts(order.getDiscounts())
                .totalAmount(order.getTotalAmount())
                .additionalNotes(order.getAdditionalNotes())
                .lines(lineDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public static List<OrderResponseDto> toOrderResponseDtoList(List<Order> orders) {
        if (orders == null) return Collections.emptyList();
        return orders.stream().map(BuyerRestMapper::toResponseDto).collect(Collectors.toList());
    }
}
