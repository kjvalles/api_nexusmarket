package application.adapters.rest.controllers;

import application.adapters.config.OpenApiConfig;
import application.adapters.rest.dtos.requests.CreateProductRequestDto;
import application.adapters.rest.dtos.requests.UpdateProductRequestDto;
import application.adapters.rest.dtos.responses.ProductResponseDto;
import application.adapters.rest.dtos.responses.ProductStockResponseDto;
import application.adapters.rest.mappers.SellerRestMapper;
import application.adapters.security.AuthenticatedUserPrincipal;
import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.ports.in.SellerPort;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para operaciones del rol Vendedor (SELLER).
 */
@Tag(name = "3. Rol Vendedor (SELLER)", description = "Endpoints para creación y gestión de productos en catálogo, estados y consulta de existencias propias")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerPort sellerPort;

    @Operation(summary = "Crear nuevo producto", description = "Registra un producto en el catálogo NoSQL con variantes opcionales (SKUs, colores, precios adicionales).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de producto o variantes inválidos")
    })
    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> createProduct(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody CreateProductRequestDto request) {
        String vendorId = resolveVendorId(principal);
        Product product = SellerRestMapper.toDomain(request, vendorId);
        Product created = sellerPort.createProduct(vendorId, product);
        return ResponseEntity.status(HttpStatus.CREATED).body(SellerRestMapper.toResponseDto(created));
    }

    @Operation(summary = "Actualizar producto existente", description = "Modifica atributos base de un producto previamente registrado por el vendedor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado o no pertenece al vendedor")
    })
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequestDto request) {
        String vendorId = resolveVendorId(principal);
        Product product = SellerRestMapper.toDomain(request, id, vendorId);
        Product updated = sellerPort.updateProduct(vendorId, product);
        return ResponseEntity.ok(SellerRestMapper.toResponseDto(updated));
    }

    @Operation(summary = "Publicar producto", description = "Cambia el estado del producto a PUBLICADO para hacerlo visible en el catálogo de compradores.")
    @ApiResponse(responseCode = "200", description = "Producto publicado correctamente")
    @PatchMapping("/products/{id}/publish")
    public ResponseEntity<Void> publishProduct(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable String id) {
        String vendorId = resolveVendorId(principal);
        sellerPort.publishProduct(vendorId, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Suspender producto", description = "Cambia el estado del producto a SUSPENDIDO para ocultarlo del catálogo público temporalmente.")
    @ApiResponse(responseCode = "200", description = "Producto suspendido correctamente")
    @PatchMapping("/products/{id}/suspend")
    public ResponseEntity<Void> suspendProduct(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable String id) {
        String vendorId = resolveVendorId(principal);
        sellerPort.suspendProduct(vendorId, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar productos del vendedor", description = "Consulta todos los productos registrados por el vendedor autenticado.")
    @ApiResponse(responseCode = "200", description = "Listado de productos obtenido")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getMyProducts(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        String vendorId = resolveVendorId(principal);
        List<Product> products = sellerPort.getMyProducts(vendorId);
        return ResponseEntity.ok(SellerRestMapper.toResponseDtoList(products));
    }

    @Operation(summary = "Consultar inventario propio", description = "Consulta las existencias disponibles y reservadas de los productos del vendedor en las distintas bodegas.")
    @ApiResponse(responseCode = "200", description = "Saldos de inventario obtenidos")
    @GetMapping("/inventory")
    public ResponseEntity<List<ProductStockResponseDto>> getMyInventory(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        String vendorId = resolveVendorId(principal);
        List<ProductStock> stocks = sellerPort.getMyInventory(vendorId);
        return ResponseEntity.ok(SellerRestMapper.toStockResponseDtoList(stocks));
    }

    private String resolveVendorId(AuthenticatedUserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new IllegalArgumentException("Usuario no autenticado.");
        }
        return principal.getUser().getId();
    }
}
