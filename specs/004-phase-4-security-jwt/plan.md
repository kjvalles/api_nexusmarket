# [PLAN-004] Plan Técnico — Fase 4: Seguridad y JWT (Adapters / Security)

## 1. Evidencia y Arquitectura Existente
- **Estado Actual:**
  - En `nexusmarcket/src/main/resources/application.properties` se encuentran declaradas las variables:
    - `jwt.secret=${JWT_SECRET:TDEATDEATDEATDEATDEATDEATDEATDEA}`
    - `jwt.expiration-ms=${JWT_EXPIRATION_MS:3600000}`
  - En `pom.xml` están declaradas las dependencias:
    - `spring-boot-starter-security`
    - `io.jsonwebtoken:jjwt-api:0.12.6`, `jjwt-impl`, `jjwt-jackson`
  - En `application.domain.ports.out` existen los contratos:
    - [`PasswordServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/PasswordServicePort.java) (`encode`, `matches`)
    - [`JwtServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/JwtServicePort.java) (`generateToken`, `validateToken`, `extractUserId`, `extractTokenVersion`, `reconstructUser`)
  - No existe implementación concreta en `application.adapters.security`.
- **Arquitectura de Referencia:**
  - El proyecto guía (`construccion_de_software_2_2026_2/bank`) implementa en `adapters/security`:
    - `BCryptPasswordServiceAdapter` implementando `PasswordServicePort`.
    - `JwtProvider` implementando `JwtServicePort` con JJWT 0.12.6.
    - `AuthenticatedUserPrincipal` implementando `UserDetails`.
    - `JwtAuthenticationFilter` (extendiendo `OncePerRequestFilter`) verificando vigencia y versión de token (`ver`).
    - `SecurityConfig` configurando Spring Security en modo `STATELESS` con autorización estricta por rol (`hasRole(...)`).

## 2. Cambios Propuestos y Diseño Proporcional

### 2.1 Adaptador de Cifrado (`BCryptPasswordServiceAdapter.java`)
- **Paquete:** `application.adapters.security`
- **Anotación:** `@Component`
- **Inyección:** `PasswordEncoder` (inyectado desde `SecurityConfig`)
- **Implementa:** `PasswordServicePort`
- **Comportamiento:**
  - `encode(CharSequence rawPassword)`: Aplica hash seguro con salt mediante `passwordEncoder.encode(rawPassword)`.
  - `matches(CharSequence rawPassword, String encodedPassword)`: Valida correspondencia retornando `false` si cualquiera es nulo o vacío.

### 2.2 Adaptador de Emisión y Validación JWT (`JwtServiceAdapter.java`)
- **Paquete:** `application.adapters.security`
- **Anotación:** `@Component`
- **Inyección:** Propiedades `@Value("${jwt.secret}")` y `@Value("${jwt.expiration-ms}")`
- **Algoritmo Criptográfico:** HMAC-SHA256 con clave secreta mínima de 256 bits (`Keys.hmacShaKeyFor`).
- **Claims Estructurados:**
  - `sub`: Identificador de usuario (`user.getId()`).
  - `ver`: Versión del token (`user.getAuthTokenVersion()`).
  - `role`: Rol del usuario (`user.getRole().name()`).
  - `email`: Correo electrónico (`user.getEmail()`).
  - `status`: Código de estado del usuario (`user.getStatus().getCode()`).
  - `iat`: Fecha de emisión (`Instant.now()`).
  - `exp`: Fecha de expiración (`Instant.now().plusMillis(expirationMs)`).
  - `jti`: Identificador único de token (`UUID.randomUUID().toString()`).
- **Métodos:**
  - `generateToken(User user)`: Construye y firma el token con JJWT 0.12.6.
  - `validateToken(String token)`: Valida firma y expiración retornando `boolean`.
  - `extractUserId(String token)`: Extrae el `sub`.
  - `extractTokenVersion(String token)`: Extrae el claim `ver`.
  - `reconstructUser(String token)`: Reconstruye un `User` liviano desde los claims sin acceso a base de datos.

### 2.3 Principal de Usuario (`AuthenticatedUserPrincipal.java`)
- **Paquete:** `application.adapters.security`
- **Implementa:** `UserDetails`
- **Encapsulación:** Instancia de `User` de dominio.
- **Autoridades:** Asigna `SimpleGrantedAuthority("ROLE_" + user.getRole().name())`.
- **Estado de Cuenta:** Refleja `user.isActive()` en `isEnabled()`, `isAccountNonLocked()` e `isAccountNonExpired()`.

### 2.4 Filtro de Intercepción JWT (`JwtAuthenticationFilter.java`)
- **Paquete:** `application.adapters.security`
- **Anotación:** `@Component`
- **Extiende:** `OncePerRequestFilter`
- **Inyección:** `JwtServicePort`
- **Flujo:**
  1. Extrae encabezado `Authorization: Bearer <token>`.
  2. Si existe y `validateToken(token)` es verdadero:
     - Reconstruye `User` y extrae `tokenVersion`.
     - Verifica si el usuario está activo y si la versión del claim coincide con `user.getAuthTokenVersion()`.
     - Si coincide: establece `UsernamePasswordAuthenticationToken` en `SecurityContextHolder`.
     - Si la versión es obsoleta o el usuario está inactivo: limpia `SecurityContextHolder` para forzar rechazo 401/403.
  3. Continúa la cadena con `filterChain.doFilter(request, response)`.

### 2.5 Configuración de Seguridad (`SecurityConfig.java`)
- **Paquete:** `application.adapters.security`
- **Anotaciones:** `@Configuration`, `@EnableWebSecurity`
- **Beans Expuestos:**
  - `SecurityFilterChain`:
    - `csrf.disable()`
    - `sessionManagement(STATELESS)`
    - Rutas públicas: `/api/v1/auth/**`, `/api/v1/public/**`, `/actuator/**`, `/swagger-ui/**`.
    - Rutas protegidas por rol:
      - `/api/v1/buyer/**` -> `hasRole("BUYER")`
      - `/api/v1/seller/**` -> `hasRole("SELLER")`
      - `/api/v1/logistics/**` -> `hasRole("LOGISTICS_OPERATOR")`
      - `/api/v1/admin/**` -> `hasRole("ADMINISTRATOR")`
      - `/api/v1/supervisor/**` -> `hasRole("SUPERVISOR")`
    - `addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`
  - `PasswordEncoder`: Retorna `new BCryptPasswordEncoder()`.
  - `CorsConfigurationSource`: Configura orígenes permitidos con `FRONTEND_ORIGIN`.

## 3. Contratos y Compatibilidad (Contracts & Compatibility)
- **Contratos:** Las clases implementan directamente [`PasswordServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/PasswordServicePort.java) y [`JwtServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/JwtServicePort.java).
- **Compatibilidad Spring:** La configuración reemplaza la necesidad de mocks en `TestOutPortsConfig` para los puertos de seguridad, proveyendo beans reales al contexto.

## 4. UI & Interaction Design
- **N/A Justificado:** Componentes de infraestructura y seguridad HTTP backend.

## 5. Verificación
- **Pruebas Unitarias de Seguridad (`src/test/java/application/adapters/security/`)**:
  - `BCryptPasswordServiceAdapterTest`: Hashing con salt, verificación de clave correcta y rechazo de clave errónea.
  - `JwtServiceAdapterTest`: Emisión de token, extracción de claims (`sub`, `ver`, `role`), validación de firma y expiración.
  - `JwtAuthenticationFilterTest`: Intercepción de header `Bearer`, autenticación exitosa e invalidación ante versión de token obsoleta.
- **Quality Gate:** Ejecución en contenedor:
  ```powershell
  docker compose exec -T nexusmarcket-app mvn test
  ```
  Debe resultar en `BUILD SUCCESS` con 0 errores y 0 fallos.

## 6. Estrategia de Reversión (Rollback)
- Si los cambios de seguridad rompen el contexto o las pruebas:
  1. Revertir mediante Git los archivos en `application/adapters/security/`.
  2. Ejecutar `docker compose exec -T nexusmarcket-app mvn test` para confirmar el restablecimiento al estado limpio anterior.
