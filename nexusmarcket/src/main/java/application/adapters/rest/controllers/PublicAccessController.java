package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.LoginRequestDto;
import application.adapters.rest.dtos.requests.RegisterBuyerRequestDto;
import application.adapters.rest.dtos.responses.AuthResponseDto;
import application.adapters.rest.dtos.responses.BuyerResponseDto;
import application.adapters.rest.dtos.responses.ProductResponseDto;
import application.adapters.rest.mappers.AuthRestMapper;
import application.adapters.security.AuthenticatedUserPrincipal;
import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.in.PublicAccessPort;
import application.domain.ports.out.JwtServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para operaciones públicas y autenticación.
 */
@Tag(name = "1. Autenticación y Catálogo Público", description = "Endpoints de acceso libre para autenticación JWT, autoregistro y consulta de productos")
@RestController
public class PublicAccessController {

    private final PublicAccessPort publicAccessPort;
    private final JwtServicePort jwtServicePort;
    private final long expirationMs;

    public PublicAccessController(
            PublicAccessPort publicAccessPort,
            JwtServicePort jwtServicePort,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.publicAccessPort = publicAccessPort;
        this.jwtServicePort = jwtServicePort;
        this.expirationMs = expirationMs;
    }

    @Operation(summary = "Iniciar sesión en NexusMarket", description = "Autentica con email/documento y contraseña. Retorna un token Bearer JWT con roles y datos de usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos o incompletos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario inactivo")
    })
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        String token = publicAccessPort.login(request.getEmailOrDocument(), request.getPassword());
        User user = jwtServicePort.reconstructUser(token);
        long expiresInSeconds = expirationMs / 1000;
        AuthResponseDto response = AuthRestMapper.toAuthResponseDto(token, user, expiresInSeconds);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cerrar sesión activa", description = "Invalida la versión del token JWT del usuario autenticado (bumpAuthTokenVersion).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sesión cerrada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token JWT no proporcionado o inválido")
    })
    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        if (principal != null && principal.getUser() != null) {
            publicAccessPort.logout(principal.getUser().getId());
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Registro público de nuevo comprador", description = "Crea una nueva cuenta de comprador con rol BUYER, dirección principal y saldo virtual inicial.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comprador registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en campos obligatorios o formato de email")
    })
    @PostMapping("/api/v1/auth/register/buyer")
    public ResponseEntity<BuyerResponseDto> registerBuyer(@Valid @RequestBody RegisterBuyerRequestDto request) {
        Buyer buyerDomain = AuthRestMapper.toDomain(request);
        Buyer saved = publicAccessPort.registerBuyer(buyerDomain, request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthRestMapper.toResponseDto(saved));
    }

    @Operation(summary = "Consultar catálogo público de productos", description = "Retorna la lista de todos los productos en estado PUBLICADO con sus variantes activas.")
    @ApiResponse(responseCode = "200", description = "Catálogo consultado exitosamente")
    @GetMapping("/api/v1/public/products")
    public ResponseEntity<List<ProductResponseDto>> getPublicCatalog() {
        List<Product> products = publicAccessPort.getPublicCatalog();
        return ResponseEntity.ok(AuthRestMapper.toProductResponseDtoList(products));
    }

    @Operation(summary = "Consultar detalle público de un producto", description = "Retorna el detalle completo de un producto por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado o no disponible")
    })
    @GetMapping("/api/v1/public/products/{id}")
    public ResponseEntity<ProductResponseDto> getPublicProductDetails(@PathVariable String id) {
        Product product = publicAccessPort.getPublicProductDetails(id);
        return ResponseEntity.ok(AuthRestMapper.toProductResponseDto(product));
    }
}
