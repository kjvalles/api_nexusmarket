package application.adapters.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración OpenAPI 3.0 / Swagger UI para la plataforma NexusMarket.
 */
@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NexusMarket E-Commerce Platform API")
                        .version("1.0.0")
                        .description("Documentación interactiva de la API RESTful de NexusMarket desarrollada bajo "
                                + "Arquitectura Hexagonal y Domain-Driven Design (DDD).\n\n"
                                + "Permite explorar y probar interactivamente los endpoints según los roles del sistema:\n"
                                + "- **Público / Auth**: Login, registro de compradores y catálogo.\n"
                                + "- **Comprador (BUYER)**: Carrito de compras, checkout, pedidos y cancelación.\n"
                                + "- **Vendedor (SELLER)**: Catálogo propio, publicación, suspensión e inventario.\n"
                                + "- **Operador Logístico (LOGISTICS_OPERATOR)**: Entradas a bodega, ajustes kardex, despacho y entrega.\n"
                                + "- **Administrador (ADMINISTRATOR)**: Alta de vendedores, bodegas, estados de cuenta y listados.\n"
                                + "- **Supervisor (SUPERVISOR)**: Bitácora de auditoría, trazabilidad de órdenes y existencias globales.")
                        .contact(new Contact()
                                .name("NexusMarket Engineering Team")
                                .email("soporte@nexusmarcket.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese el token JWT obtenido del endpoint POST /api/v1/auth/login. "
                                        + "Swagger incluirá automáticamente la cabecera 'Authorization: Bearer <token>' en cada solicitud protegida.")));
    }
}
