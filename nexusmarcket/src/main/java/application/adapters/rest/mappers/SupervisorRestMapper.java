package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.AuditLogResponseDto;
import application.adapters.rest.dtos.responses.OrderResponseDto;
import application.adapters.rest.dtos.responses.ProductStockResponseDto;
import application.domain.models.AuditLog;
import application.domain.models.Order;
import application.domain.models.ProductStock;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class SupervisorRestMapper {

    private SupervisorRestMapper() {}

    public static AuditLogResponseDto toResponseDto(AuditLog log) {
        if (log == null) return null;
        return AuditLogResponseDto.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorRole(log.getActorRole())
                .action(log.getAction())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }

    public static List<AuditLogResponseDto> toAuditLogResponseDtoList(List<AuditLog> logs) {
        if (logs == null) return Collections.emptyList();
        return logs.stream().map(SupervisorRestMapper::toResponseDto).collect(Collectors.toList());
    }

    public static OrderResponseDto toResponseDto(Order order) {
        return BuyerRestMapper.toResponseDto(order);
    }

    public static List<OrderResponseDto> toOrderResponseDtoList(List<Order> orders) {
        if (orders == null) return Collections.emptyList();
        return orders.stream().map(SupervisorRestMapper::toResponseDto).collect(Collectors.toList());
    }

    public static ProductStockResponseDto toResponseDto(ProductStock stock) {
        return SellerRestMapper.toResponseDto(stock);
    }

    public static List<ProductStockResponseDto> toStockResponseDtoList(List<ProductStock> stocks) {
        if (stocks == null) return Collections.emptyList();
        return stocks.stream().map(SupervisorRestMapper::toResponseDto).collect(Collectors.toList());
    }
}
