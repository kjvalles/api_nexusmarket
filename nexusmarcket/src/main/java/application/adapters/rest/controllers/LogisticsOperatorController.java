package application.adapters.rest.controllers;

import application.adapters.config.OpenApiConfig;
import application.adapters.rest.dtos.requests.DispatchOrderRequestDto;
import application.adapters.rest.dtos.requests.RegisterStockMovementRequestDto;
import application.adapters.rest.dtos.responses.ProductStockResponseDto;
import application.adapters.rest.dtos.responses.WarehouseResponseDto;
import application.adapters.rest.mappers.AdminRestMapper;
import application.adapters.rest.mappers.LogisticsRestMapper;
import application.adapters.security.AuthenticatedUserPrincipal;
import application.domain.models.ProductStock;
import application.domain.models.Warehouse;
import application.domain.ports.in.LogisticsOperatorPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para operaciones del rol Operador Logístico (LOGISTICS_OPERATOR).
 */
@Tag(name = "4. Rol Operador Logístico (LOGISTICS_OPERATOR)", description = "Endpoints para operaciones de bodega, kardex, despacho y confirmación de entrega")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/logistics")
@RequiredArgsConstructor
public class LogisticsOperatorController {

    private final LogisticsOperatorPort logisticsOperatorPort;

    @Operation(summary = "Registrar entrada de mercancía a bodega", description = "Incrementa existencias disponibles en la bodega especificada y registra el movimiento kardex.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de movimiento o cantidad no válida")
    })
    @PostMapping("/inventory/inflow")
    public ResponseEntity<ProductStockResponseDto> registerInflow(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody RegisterStockMovementRequestDto request) {
        String operatorId = resolveOperatorId(principal);
        ProductStock stock = logisticsOperatorPort.registerInflow(
                operatorId,
                request.getProductId(),
                request.getVariantId(),
                request.getWarehouseId(),
                request.getQuantity(),
                request.getNotes()
        );
        return ResponseEntity.ok(LogisticsRestMapper.toResponseDto(stock));
    }

    @Operation(summary = "Registrar ajuste o merma de inventario", description = "Aplica un ajuste positivo o negativo en existencias, garantizando que el stock resultante no sea negativo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ajuste registrado exitosamente"),
            @ApiResponse(responseCode = "409", description = "El ajuste generaría existencias negativas (violación de invariante)")
    })
    @PostMapping("/inventory/adjustment")
    public ResponseEntity<ProductStockResponseDto> registerAdjustment(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody RegisterStockMovementRequestDto request) {
        String operatorId = resolveOperatorId(principal);
        ProductStock stock = logisticsOperatorPort.registerAdjustment(
                operatorId,
                request.getProductId(),
                request.getVariantId(),
                request.getWarehouseId(),
                request.getQuantity(),
                request.getNotes()
        );
        return ResponseEntity.ok(LogisticsRestMapper.toResponseDto(stock));
    }

    @Operation(summary = "Consultar existencias de una bodega", description = "Retorna todos los saldos de inventario de los productos almacenados en la bodega indicada.")
    @ApiResponse(responseCode = "200", description = "Saldos de bodega obtenidos")
    @GetMapping("/warehouses/{warehouseId}/stock")
    public ResponseEntity<List<ProductStockResponseDto>> getWarehouseStock(@PathVariable String warehouseId) {
        List<ProductStock> stocks = logisticsOperatorPort.getWarehouseStock(warehouseId);
        return ResponseEntity.ok(LogisticsRestMapper.toResponseDtoList(stocks));
    }

    @Operation(summary = "Listar bodegas operativas", description = "Retorna el listado de bodegas registradas para consulta y operación de existencias.")
    @ApiResponse(responseCode = "200", description = "Listado de bodegas obtenido")
    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseResponseDto>> listWarehouses() {
        List<Warehouse> warehouses = logisticsOperatorPort.listWarehouses();
        return ResponseEntity.ok(AdminRestMapper.toWarehouseResponseDtoList(warehouses));
    }

    @Operation(summary = "Despachar pedido con guía de transporte", description = "Transiciona el estado de la orden a DISPATCHED y asigna el número de seguimiento correspondiente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido despachado exitosamente"),
            @ApiResponse(responseCode = "409", description = "La orden no está en estado válido para ser despachada")
    })
    @PostMapping("/orders/{orderId}/dispatch")
    public ResponseEntity<Void> dispatchOrder(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable Long orderId,
            @Valid @RequestBody DispatchOrderRequestDto request) {
        String operatorId = resolveOperatorId(principal);
        logisticsOperatorPort.dispatchOrder(operatorId, orderId, request.getTrackingNumber());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Confirmar entrega final de pedido", description = "Transiciona el estado del pedido a DELIVERED tras la entrega confirmada al comprador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrega confirmada exitosamente"),
            @ApiResponse(responseCode = "409", description = "La orden debe estar despachada antes de poder entregarse")
    })
    @PostMapping("/orders/{orderId}/deliver")
    public ResponseEntity<Void> deliverOrder(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable Long orderId) {
        String operatorId = resolveOperatorId(principal);
        logisticsOperatorPort.deliverOrder(operatorId, orderId);
        return ResponseEntity.ok().build();
    }

    private String resolveOperatorId(AuthenticatedUserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new IllegalArgumentException("Usuario no autenticado.");
        }
        return principal.getUser().getId();
    }
}
