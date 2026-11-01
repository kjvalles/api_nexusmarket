package application.adapters.rest;

import application.adapters.rest.controllers.AdministratorController;
import application.adapters.rest.controllers.BuyerController;
import application.adapters.rest.controllers.LogisticsOperatorController;
import application.adapters.rest.controllers.PublicAccessController;
import application.adapters.rest.controllers.SellerController;
import application.adapters.rest.controllers.SupervisorController;
import application.adapters.rest.dtos.requests.AddItemToCartRequestDto;
import application.adapters.rest.dtos.requests.ChangeUserStatusRequestDto;
import application.adapters.rest.dtos.requests.CheckoutOrderRequestDto;
import application.adapters.rest.dtos.requests.CreateProductRequestDto;
import application.adapters.rest.dtos.requests.CreateUserRequestDto;
import application.adapters.rest.dtos.requests.CreateWarehouseRequestDto;
import application.adapters.rest.dtos.requests.DispatchOrderRequestDto;
import application.adapters.rest.dtos.requests.LoginRequestDto;
import application.adapters.rest.dtos.requests.RegisterBuyerRequestDto;
import application.adapters.rest.dtos.requests.RegisterSellerRequestDto;
import application.adapters.rest.dtos.requests.RegisterStockMovementRequestDto;
import application.adapters.rest.exceptions.GlobalExceptionHandler;
import application.adapters.security.AuthenticatedUserPrincipal;
import application.domain.enums.UserRole;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidCredentialsException;
import application.domain.exceptions.NegativeStockException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.exceptions.UnauthorizedRoleOperationException;
import application.domain.models.AuditLog;
import application.domain.models.Buyer;
import application.domain.models.DefaultUser;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.ProductStock;
import application.domain.models.Seller;
import application.domain.models.ShoppingCart;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.AdministratorPort;
import application.domain.ports.in.BuyerPort;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.ports.in.PublicAccessPort;
import application.domain.ports.in.SellerPort;
import application.domain.ports.in.SupervisorPort;
import application.domain.ports.out.JwtServicePort;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.CartItem;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.WarehouseType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RestControllersTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PublicAccessPort publicAccessPort;

    @Mock
    private BuyerPort buyerPort;

    @Mock
    private SellerPort sellerPort;

    @Mock
    private LogisticsOperatorPort logisticsOperatorPort;

    @Mock
    private AdministratorPort administratorPort;

    @Mock
    private SupervisorPort supervisorPort;

    @Mock
    private JwtServicePort jwtServicePort;

    private MockMvc mockMvc;
    private AuthenticatedUserPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        DefaultUser user = new DefaultUser(UserRole.BUYER);
        user.setId("usr-test-123");
        user.setEmail("user@nexusmarcket.com");
        testPrincipal = new AuthenticatedUserPrincipal(user);

        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                return testPrincipal;
            }
        };

        PublicAccessController publicAccessController = new PublicAccessController(publicAccessPort, jwtServicePort, 3600000L);
        BuyerController buyerController = new BuyerController(buyerPort);
        SellerController sellerController = new SellerController(sellerPort);
        LogisticsOperatorController logisticsController = new LogisticsOperatorController(logisticsOperatorPort);
        AdministratorController adminController = new AdministratorController(administratorPort);
        SupervisorController supervisorController = new SupervisorController(supervisorPort);

        mockMvc = MockMvcBuilders
                .standaloneSetup(publicAccessController, buyerController, sellerController, logisticsController, adminController, supervisorController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(principalResolver)
                .build();
    }

    // --- PublicAccessController Tests ---

    @Test
    @DisplayName("AC-01: Login exitoso retorna 200 con token Bearer")
    void testLoginSuccess() throws Exception {
        LoginRequestDto req = new LoginRequestDto("user@nexusmarcket.com", "Secret123*");
        when(publicAccessPort.login("user@nexusmarcket.com", "Secret123*")).thenReturn("jwt.token.test");
        when(jwtServicePort.reconstructUser("jwt.token.test")).thenReturn(testPrincipal.getUser());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.test"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("user@nexusmarcket.com"));
    }

    @Test
    @DisplayName("AC-01 / AC-08: Login con credenciales inválidas retorna 401 UNAUTHORIZED")
    void testLoginInvalidCredentials() throws Exception {
        LoginRequestDto req = new LoginRequestDto("user@nexusmarcket.com", "WrongPassword");
        when(publicAccessPort.login(anyString(), anyString()))
                .thenThrow(new InvalidCredentialsException("Credenciales incorrectas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "trace-login-401")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("X-Request-Id", "trace-login-401"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"));
    }

    @Test
    @DisplayName("AC-01: Registro de comprador exitoso retorna 201 CREATED")
    void testRegisterBuyerSuccess() throws Exception {
        RegisterBuyerRequestDto req = RegisterBuyerRequestDto.builder()
                .identityDocument("CC100200300")
                .fullName("Juan Perez")
                .email("juan.perez@test.com")
                .password("Password123*")
                .street("Calle 100 #15-20")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110111")
                .country("Colombia")
                .additionalInstructions("Apto 502")
                .build();

        Buyer saved = new Buyer();
        saved.setId("buyer-new-1");
        saved.setIdentityDocument("CC100200300");
        saved.setFullName("Juan Perez");
        saved.setEmail("juan.perez@test.com");
        saved.setPrimaryAddress(new Address("Calle 100 #15-20", "Bogotá", "Cundinamarca", "110111", "Colombia", "Apto 502"));

        when(publicAccessPort.registerBuyer(any(Buyer.class), eq("Password123*"))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/auth/register/buyer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("buyer-new-1"))
                .andExpect(jsonPath("$.email").value("juan.perez@test.com"));
    }

    @Test
    @DisplayName("AC-01 / AC-08: Registro de comprador con email inválido retorna 400 BAD REQUEST")
    void testRegisterBuyerValidationFailure() throws Exception {
        RegisterBuyerRequestDto req = RegisterBuyerRequestDto.builder()
                .identityDocument("CC100200300")
                .fullName("Juan Perez")
                .email("correo-invalido-sin-arroba")
                .password("short")
                .street("Calle 100")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110111")
                .country("Colombia")
                .build();

        mockMvc.perform(post("/api/v1/auth/register/buyer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("AC-01: Consulta de catálogo público retorna 200 con lista de productos")
    void testGetPublicCatalog() throws Exception {
        Product p = new Product("vendor-1", "Laptop Pro", "Electronics", BigDecimal.valueOf(1500.0), ProductType.PHYSICAL);
        p.setId("prod-1");
        when(publicAccessPort.getPublicCatalog()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/v1/public/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("prod-1"))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro"));
    }

    @Test
    @DisplayName("AC-01 / AC-08: Detalle de producto no encontrado retorna 404 NOT FOUND")
    void testGetPublicProductNotFound() throws Exception {
        when(publicAccessPort.getPublicProductDetails("prod-999"))
                .thenThrow(new ProductNotAvailableException("Producto no encontrado"));

        mockMvc.perform(get("/api/v1/public/products/prod-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // --- BuyerController Tests ---

    @Test
    @DisplayName("AC-02: Consultar carrito retorna 200 con items y totales")
    void testGetCart() throws Exception {
        ShoppingCart cart = new ShoppingCart("usr-test-123");
        cart.setId("cart-1");
        CartItem item = new CartItem("prod-1", "var-1", "Laptop Pro", "SKU-LAP", 2, BigDecimal.valueOf(1500.0), null);
        cart.addItem(item);
        when(buyerPort.getCart("usr-test-123")).thenReturn(cart);

        mockMvc.perform(get("/api/v1/buyer/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cart-1"))
                .andExpect(jsonPath("$.buyerId").value("usr-test-123"))
                .andExpect(jsonPath("$.totalItems").value(2));
    }

    @Test
    @DisplayName("AC-02: Checkout de orden exitoso retorna 201 CREATED")
    void testCheckoutOrderSuccess() throws Exception {
        CheckoutOrderRequestDto req = CheckoutOrderRequestDto.builder()
                .street("Cra 7 #72-10")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110221")
                .country("Colombia")
                .additionalNotes("Entregar en recepción")
                .build();

        Order order = new Order("usr-test-123");
        order.setId(501L);
        order.setTotalAmount(BigDecimal.valueOf(3000.0));
        when(buyerPort.checkoutCart(eq("usr-test-123"), any(Address.class), anyString())).thenReturn(order);

        mockMvc.perform(post("/api/v1/buyer/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(501))
                .andExpect(jsonPath("$.buyerId").value("usr-test-123"));
    }

    @Test
    @DisplayName("AC-02 / AC-08: Checkout falla por inventario insuficiente retorna 409 CONFLICT")
    void testCheckoutOrderNegativeStock() throws Exception {
        CheckoutOrderRequestDto req = CheckoutOrderRequestDto.builder()
                .street("Cra 7 #72-10")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110221")
                .country("Colombia")
                .build();

        when(buyerPort.checkoutCart(anyString(), any(Address.class), any()))
                .thenThrow(new NegativeStockException("Stock insuficiente para el producto"));

        mockMvc.perform(post("/api/v1/buyer/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Stock insuficiente para el producto"));
    }

    @Test
    @DisplayName("AC-02: Vaciar carrito retorna 204 NO CONTENT")
    void testClearCart() throws Exception {
        doNothing().when(buyerPort).clearCart("usr-test-123");

        mockMvc.perform(delete("/api/v1/buyer/cart"))
                .andExpect(status().isNoContent());
    }

    // --- SellerController Tests ---

    @Test
    @DisplayName("AC-03: Crear producto como vendedor retorna 201 CREATED")
    void testCreateProduct() throws Exception {
        CreateProductRequestDto req = CreateProductRequestDto.builder()
                .name("Teclado Mecánico")
                .category("Periféricos")
                .basePrice(BigDecimal.valueOf(120.0))
                .productType("PHYSICAL")
                .build();

        Product created = new Product("usr-test-123", "Teclado Mecánico", "Periféricos", BigDecimal.valueOf(120.0), ProductType.PHYSICAL);
        created.setId("prod-kbd-1");
        when(sellerPort.createProduct(eq("usr-test-123"), any(Product.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/seller/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("prod-kbd-1"))
                .andExpect(jsonPath("$.name").value("Teclado Mecánico"));
    }

    @Test
    @DisplayName("AC-03: Publicar producto retorna 200 OK")
    void testPublishProduct() throws Exception {
        doNothing().when(sellerPort).publishProduct("usr-test-123", "prod-kbd-1");

        mockMvc.perform(patch("/api/v1/seller/products/prod-kbd-1/publish"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("AC-03: Consultar inventario propio retorna 200 OK con existencias")
    void testGetSellerInventory() throws Exception {
        ProductStock stock = new ProductStock("prod-kbd-1", null, "wh-1", 50);
        stock.setId(10L);
        when(sellerPort.getMyInventory("usr-test-123")).thenReturn(List.of(stock));

        mockMvc.perform(get("/api/v1/seller/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("prod-kbd-1"))
                .andExpect(jsonPath("$[0].availableQuantity").value(50));
    }

    // --- LogisticsOperatorController Tests ---

    @Test
    @DisplayName("AC-04: Registrar entrada a bodega retorna 200 OK con stock actualizado")
    void testRegisterInflow() throws Exception {
        RegisterStockMovementRequestDto req = RegisterStockMovementRequestDto.builder()
                .productId("prod-kbd-1")
                .warehouseId("wh-central")
                .quantity(30)
                .notes("Ingreso por orden de compra OC-99")
                .build();

        ProductStock updated = new ProductStock("prod-kbd-1", null, "wh-central", 80);
        updated.setId(11L);
        when(logisticsOperatorPort.registerInflow(eq("usr-test-123"), eq("prod-kbd-1"), any(), eq("wh-central"), eq(30), anyString()))
                .thenReturn(updated);

        mockMvc.perform(post("/api/v1/logistics/inventory/inflow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").doesNotExist())
                .andExpect(jsonPath("$.availableQuantity").value(80));
    }

    @Test
    @DisplayName("AC-04: Despachar orden asignando guía retorna 200 OK")
    void testDispatchOrder() throws Exception {
        DispatchOrderRequestDto req = new DispatchOrderRequestDto("GUIA-FEDEX-998877");
        doNothing().when(logisticsOperatorPort).dispatchOrder("usr-test-123", 501L, "GUIA-FEDEX-998877");

        mockMvc.perform(post("/api/v1/logistics/orders/501/dispatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    // --- AdministratorController Tests ---

    @Test
    @DisplayName("AC-05: Dar de alta vendedor comercial retorna 201 CREATED")
    void testRegisterSeller() throws Exception {
        RegisterSellerRequestDto req = RegisterSellerRequestDto.builder()
                .identityDocument("NIT-900123456")
                .fullName("Tech Imports SAS")
                .email("contacto@techimports.com")
                .initialPassword("Tech2026*")
                .businessName("Tech Imports")
                .taxId("900123456-1")
                .businessDescription("Importación de tecnología")
                .build();

        Seller saved = new Seller();
        saved.setId("seller-tech-1");
        saved.setBusinessName("Tech Imports");
        saved.setEmail("contacto@techimports.com");
        when(administratorPort.registerSeller(eq("usr-test-123"), any(Seller.class), eq("Tech2026*"))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/admin/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("seller-tech-1"))
                .andExpect(jsonPath("$.businessName").value("Tech Imports"));
    }

    @Test
    @DisplayName("AC-05: Modificar estado de usuario retorna 200 OK")
    void testChangeUserStatus() throws Exception {
        ChangeUserStatusRequestDto req = new ChangeUserStatusRequestDto("SUSPENDED");
        doNothing().when(administratorPort).changeUserStatus("usr-test-123", "target-usr-88", "SUSPENDED");

        mockMvc.perform(patch("/api/v1/admin/users/target-usr-88/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("AC-05: Registrar nuevo usuario interno retorna 201 CREATED")
    void testRegisterUser_Success() throws Exception {
        CreateUserRequestDto req = CreateUserRequestDto.builder()
                .identityDocument("CC123456789")
                .fullName("Carlos Administrador")
                .email("carlos.admin@nexusmarket.com")
                .password("AdminPass2026*")
                .role(UserRole.ADMINISTRATOR)
                .build();

        DefaultUser saved = new DefaultUser(UserRole.ADMINISTRATOR);
        saved.setId("usr-admin-99");
        saved.setIdentityDocument("CC123456789");
        saved.setFullName("Carlos Administrador");
        saved.setEmail("carlos.admin@nexusmarket.com");

        when(administratorPort.registerUser(eq("usr-test-123"), any(User.class), eq("AdminPass2026*"))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("usr-admin-99"))
                .andExpect(jsonPath("$.fullName").value("Carlos Administrador"))
                .andExpect(jsonPath("$.email").value("carlos.admin@nexusmarket.com"))
                .andExpect(jsonPath("$.role").value("ADMINISTRATOR"));
    }

    @Test
    @DisplayName("AC-05: Consultar usuario por ID retorna 200 OK")
    void testGetUserById_Success() throws Exception {
        DefaultUser user = new DefaultUser(UserRole.SUPERVISOR);
        user.setId("usr-sup-100");
        user.setIdentityDocument("CC987654321");
        user.setFullName("Maria Supervisora");
        user.setEmail("maria.sup@nexusmarket.com");

        when(administratorPort.getUserById("usr-sup-100")).thenReturn(user);

        mockMvc.perform(get("/api/v1/admin/users/usr-sup-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("usr-sup-100"))
                .andExpect(jsonPath("$.fullName").value("Maria Supervisora"))
                .andExpect(jsonPath("$.email").value("maria.sup@nexusmarket.com"))
                .andExpect(jsonPath("$.role").value("SUPERVISOR"));
    }

    @Test
    @DisplayName("AC-05 / AC-08: Consultar usuario por ID no existente retorna 404 NOT_FOUND")
    void testGetUserById_NotFound() throws Exception {
        when(administratorPort.getUserById("usr-non-existent"))
                .thenThrow(new EntityNotFoundException("Usuario", "usr-non-existent"));

        mockMvc.perform(get("/api/v1/admin/users/usr-non-existent")
                        .header("X-Request-Id", "trace-user-404"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("X-Request-Id", "trace-user-404"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuario con identificador 'usr-non-existent' no fue encontrado."));
    }

    // --- SupervisorController Tests ---

    @Test
    @DisplayName("AC-06: Consultar logs de auditoría retorna 200 OK")
    void testGetAuditLogs() throws Exception {
        AuditLog log = new AuditLog("usr-admin-1", "ADMINISTRATOR", "REGISTER_SELLER", "Seller", "seller-tech-1", "Alta exitosa");
        log.setId("log-001");
        when(supervisorPort.getAuditLogs("usr-test-123", "Seller")).thenReturn(List.of(log));

        mockMvc.perform(get("/api/v1/supervisor/audit-logs").param("resourceFilter", "Seller"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("log-001"))
                .andExpect(jsonPath("$[0].action").value("REGISTER_SELLER"));
    }

    // --- GlobalExceptionHandler Tests ---

    @Test
    @DisplayName("AC-08: Operación no autorizada de rol retorna 403 FORBIDDEN")
    void testUnauthorizedRoleOperation() throws Exception {
        when(buyerPort.getCart(anyString()))
                .thenThrow(new UnauthorizedRoleOperationException("El usuario no tiene permisos de comprador"));

        mockMvc.perform(get("/api/v1/buyer/cart")
                        .header("X-Request-Id", "trace-role-403"))
                .andExpect(status().isForbidden())
                .andExpect(header().string("X-Request-Id", "trace-role-403"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("El usuario no tiene permisos de comprador"));
    }
}
