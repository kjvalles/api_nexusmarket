package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.ProductStockResponseDto;
import application.domain.models.ProductStock;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class LogisticsRestMapper {

    private LogisticsRestMapper() {}

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

    public static List<ProductStockResponseDto> toResponseDtoList(List<ProductStock> stocks) {
        if (stocks == null) return Collections.emptyList();
        return stocks.stream().map(LogisticsRestMapper::toResponseDto).collect(Collectors.toList());
    }
}
