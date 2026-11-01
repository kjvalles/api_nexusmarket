package application.adapters.rest.controllers;

import application.adapters.config.OpenApiConfig;
import application.adapters.rest.dtos.requests.ChangeUserStatusRequestDto;
import application.adapters.rest.dtos.requests.CreateUserRequestDto;
import application.adapters.rest.dtos.requests.CreateWarehouseRequestDto;
import application.adapters.rest.dtos.requests.RegisterSellerRequestDto;
import application.adapters.rest.dtos.responses.SellerResponseDto;
import application.adapters.rest.dtos.responses.UserResponseDto;
import application.adapters.rest.dtos.responses.WarehouseResponseDto;
import application.adapters.rest.mappers.AdminRestMapper;
import application.adapters.security.AuthenticatedUserPrincipal;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para operaciones del rol Administrador (ADMINISTRATOR).
 */
@Tag(name = "5. Rol Administrador (ADMINISTRATOR)", description = "Endpoints de administración global: alta de vendedores, creación de bodegas y control de usuarios")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorPort administratorPort;

    @Operation(summary = "Registrar nuevo vendedor comercial", description = "Da de alta a una empresa vendedora en la plataforma con credenciales y perfil comercial.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vendedor registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de vendedor o NIT inválidos")
    })
    @PostMapping("/sellers")
    public ResponseEntity<SellerResponseDto> registerSeller(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody RegisterSellerRequestDto request) {
        String adminId = resolveAdminId(principal);
        Seller seller = AdminRestMapper.toDomain(request);
        Seller saved = administratorPort.registerSeller(adminId, seller, request.getInitialPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminRestMapper.toResponseDto(saved));
    }

    @Operation(summary = "Crear nueva bodega física", description = "Registra una nueva bodega (MARKETPLACE o SELLER) con dirección física y capacidad de almacenamiento.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bodega creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de bodega o dirección incompletos")
    })
    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseResponseDto> createWarehouse(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody CreateWarehouseRequestDto request) {
        String adminId = resolveAdminId(principal);
        Warehouse warehouse = AdminRestMapper.toDomain(request);
        Warehouse saved = administratorPort.createWarehouse(adminId, warehouse);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminRestMapper.toResponseDto(saved));
    }

    @Operation(summary = "Cambiar estado de cuenta de usuario", description = "Modifica el estado operativo (ACTIVE, SUSPENDED, BLOCKED) e invalida tokens activos del usuario afectado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado de usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario objetivo no encontrado")
    })
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<Void> changeUserStatus(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @PathVariable String userId,
            @Valid @RequestBody ChangeUserStatusRequestDto request) {
        String adminId = resolveAdminId(principal);
        administratorPort.changeUserStatus(adminId, userId, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Registrar nuevo usuario interno (Administrador, Operador, Supervisor)", description = "Permite a un administrador crear cuentas internas del sistema especificando el rol asignado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos")
    })
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> registerUser(
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody CreateUserRequestDto request) {
        String adminId = resolveAdminId(principal);
        User user = AdminRestMapper.toDomain(request);
        User savedUser = administratorPort.registerUser(adminId, user, request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminRestMapper.toResponseDto(savedUser));
    }

    @Operation(summary = "Consultar usuario por ID", description = "Retorna el detalle completo de un usuario específico registrado en el Marketplace.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle del usuario obtenido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String userId) {
        User user = administratorPort.getUserById(userId);
        return ResponseEntity.ok(AdminRestMapper.toResponseDto(user));
    }

    @Operation(summary = "Listar todos los usuarios", description = "Retorna el listado consolidado de usuarios registrados en la base de datos relacional.")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> listUsers() {
        List<User> users = administratorPort.listUsers();
        return ResponseEntity.ok(AdminRestMapper.toUserResponseDtoList(users));
    }

    @Operation(summary = "Listar todos los vendedores", description = "Retorna el listado de empresas y comercios dados de alta como vendedores.")
    @ApiResponse(responseCode = "200", description = "Listado de vendedores obtenido")
    @GetMapping("/sellers")
    public ResponseEntity<List<SellerResponseDto>> listSellers() {
        List<Seller> sellers = administratorPort.listSellers();
        return ResponseEntity.ok(AdminRestMapper.toSellerResponseDtoList(sellers));
    }

    private String resolveAdminId(AuthenticatedUserPrincipal principal) {
        if (principal == null || principal.getUser() == null) {
            throw new IllegalArgumentException("Usuario no autenticado.");
        }
        return principal.getUser().getId();
    }
}
