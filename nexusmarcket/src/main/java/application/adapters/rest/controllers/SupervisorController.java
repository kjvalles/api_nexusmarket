package application.adapters.rest.controllers;

import application.adapters.config.OpenApiConfig;
import application.adapters.rest.dtos.responses.AuditLogResponseDto;
import application.adapters.rest.dtos.responses.OrderResponseDto;
import application.adapters.rest.dtos.responses.ProductStockResponseDto;
import application.adapters.rest.mappers.SupervisorRestMapper;
import application.adapters.security.AuthenticatedUserPrincipal;
import application.domain.models.AuditLog;
import application.domain.models.Order;
import application.domain.models.ProductStock;
import application.domain.ports.in.SupervisorPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para operaciones del rol Supervisor (SUPERVISOR).
 */
@Tag(name = "6. Rol Supervisor (SUPERVISOR)", description = "Endpoints para auditoría inmutable, monitoreo de pedidos globales y resumen de inventarios")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/supervisor")
@RequiredArgsConstructor
public class SupervisorController {

    private final SupervisorPort supervisorPort;

    @Operation(summary = "Consultar bitácora de auditoría", description = "Retorna los registros inmutables de auditoría almacenados en MongoDB con filtro opcional de entidad o recurso.")
    @ApiResponse(responseCode = "200", description = "Bitácora de auditoría obtenida")
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponseDto>> getAuditLogs(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Parameter(description = "Filtro opcional por tipo de entidad (ej. Seller, Warehouse, Order)")
            @RequestParam(required = false) String resourceFilter) {
        String supervisorId = resolveSupervisorId(principal);
        List<AuditLog> logs = supervisorPort.getAuditLogs(supervisorId, resourceFilter);
        return ResponseEntity.ok(SupervisorRestMapper.toAuditLogResponseDtoList(logs));
    }

    @Operation(summary = "Consultar todas las órdenes globales", description = "Obtiene la totalidad de pedidos del sistema a nivel de plataforma para supervisión de ciclo comercial.")
    @ApiResponse(responseCode = "200", description = "Listado global de órdenes obtenido")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        String supervisorId = resolveSupervisorId(principal);
        List<Order> orders = supervisorPort.getAllOrders(supervisorId);
        return ResponseEntity.ok(SupervisorRestMapper.toOrderResponseDtoList(orders));
    }

    @Operation(summary = "Consultar resumen global de inventarios", description = "Retorna una vista consolidada de los saldos de inventario en todas las bodegas del marketplace.")
    @ApiResponse(responseCode = "200", description = "Resumen de inventario obtenido")
    @GetMapping("/inventory")
    public ResponseEntity<List<ProductStockResponseDto>> getInventorySummary(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        String supervisorId = resolveSupervisorId(principal);
        List<ProductStock> summary = supervisorPort.getInventorySummary(supervisorId);
        return ResponseEntity.ok(SupervisorRestMapper.toStockResponseDtoList(summary));
    }

    private String resolveSupervisorId(AuthenticatedUserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new IllegalArgumentException("Usuario no autenticado.");
        }
        return principal.getUser().getId();
    }
}
