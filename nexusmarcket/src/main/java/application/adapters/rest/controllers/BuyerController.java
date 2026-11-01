package application.adapters.rest.controllers;

import application.adapters.config.OpenApiConfig;
import application.adapters.rest.dtos.requests.AddItemToCartRequestDto;
import application.adapters.rest.dtos.requests.CancelOrderRequestDto;
import application.adapters.rest.dtos.requests.CheckoutOrderRequestDto;
import application.adapters.rest.dtos.responses.OrderResponseDto;
import application.adapters.rest.dtos.responses.ShoppingCartResponseDto;
import application.adapters.rest.mappers.BuyerRestMapper;
import application.adapters.security.AuthenticatedUserPrincipal;
import application.domain.models.Order;
import application.domain.models.ShoppingCart;
import application.domain.ports.in.BuyerPort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.CartItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para operaciones del rol Comprador (BUYER).
 */
@Tag(name = "2. Rol Comprador (BUYER)", description = "Endpoints para gestión del carrito de compras, checkout, historial de pedidos y cancelaciones")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerPort buyerPort;

    @Operation(summary = "Consultar carrito activo", description = "Obtiene los ítems, cantidades y totales acumulados en el carrito del comprador autenticado.")
    @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente")
    @GetMapping("/cart")
    public ResponseEntity<ShoppingCartResponseDto> getCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        String buyerId = resolveBuyerId(principal);
        ShoppingCart cart = buyerPort.getCart(buyerId);
        return ResponseEntity.ok(BuyerRestMapper.toResponseDto(cart));
    }

    @Operation(summary = "Agregar ítem al carrito", description = "Añade un producto y variante con su cantidad y precio unitario al carrito del comprador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem agregado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de ítem inválidos (cantidad menor a 1 o precio negativo)")
    })
    @PostMapping("/cart/items")
    public ResponseEntity<ShoppingCartResponseDto> addItemToCart(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody AddItemToCartRequestDto request) {
        String buyerId = resolveBuyerId(principal);
        CartItem item = BuyerRestMapper.toDomain(request);
        ShoppingCart updatedCart = buyerPort.addItemToCart(buyerId, item);
        return ResponseEntity.ok(BuyerRestMapper.toResponseDto(updatedCart));
    }

    @Operation(summary = "Eliminar ítem del carrito", description = "Remueve una variante de producto específica del carrito de compras.")
    @ApiResponse(responseCode = "200", description = "Ítem removido correctamente")
    @DeleteMapping("/cart/items/{productId}/{variantId}")
    public ResponseEntity<ShoppingCartResponseDto> removeItemFromCart(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable String productId,
            @PathVariable String variantId) {
        String buyerId = resolveBuyerId(principal);
        ShoppingCart updatedCart = buyerPort.removeItemFromCart(buyerId, productId, variantId);
        return ResponseEntity.ok(BuyerRestMapper.toResponseDto(updatedCart));
    }

    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito de compras del comprador.")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado exitosamente")
    @DeleteMapping("/cart")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        String buyerId = resolveBuyerId(principal);
        buyerPort.clearCart(buyerId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Procesar checkout de la orden", description = "Transforma los ítems del carrito en un pedido formal con cálculo de impuestos, validación de stock y dirección de entrega.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Carrito vacío o dirección incompleta"),
            @ApiResponse(responseCode = "409", description = "Conflicto por falta de stock o saldo virtual insuficiente")
    })
    @PostMapping("/orders/checkout")
    public ResponseEntity<OrderResponseDto> checkoutCart(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody CheckoutOrderRequestDto request) {
        String buyerId = resolveBuyerId(principal);
        Address address = BuyerRestMapper.toDomainAddress(request);
        Order order = buyerPort.checkoutCart(buyerId, address, request.getAdditionalNotes());
        return ResponseEntity.status(HttpStatus.CREATED).body(BuyerRestMapper.toResponseDto(order));
    }

    @Operation(summary = "Listar pedidos del comprador", description = "Consulta el historial de todas las órdenes de compra asociadas al comprador autenticado.")
    @ApiResponse(responseCode = "200", description = "Listado de órdenes obtenido exitosamente")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        String buyerId = resolveBuyerId(principal);
        List<Order> orders = buyerPort.getMyOrders(buyerId);
        return ResponseEntity.ok(BuyerRestMapper.toOrderResponseDtoList(orders));
    }

    @Operation(summary = "Consultar detalle de un pedido", description = "Obtiene la información detallada de una orden por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle del pedido obtenido"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado o no pertenece al comprador")
    })
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderDetails(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable Long orderId) {
        String buyerId = resolveBuyerId(principal);
        Order order = buyerPort.getOrderDetails(buyerId, orderId);
        return ResponseEntity.ok(BuyerRestMapper.toResponseDto(order));
    }

    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido que se encuentre en estado pendiente, liberando reservas y registrando el motivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido cancelado correctamente"),
            @ApiResponse(responseCode = "409", description = "El pedido no se puede cancelar en su estado actual (ej. ya despachado o entregado)")
    })
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderRequestDto request) {
        String buyerId = resolveBuyerId(principal);
        buyerPort.cancelOrder(buyerId, orderId, request.getReason());
        return ResponseEntity.noContent().build();
    }

    private String resolveBuyerId(AuthenticatedUserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new IllegalArgumentException("Usuario no autenticado.");
        }
        return principal.getUser().getId();
    }
}
