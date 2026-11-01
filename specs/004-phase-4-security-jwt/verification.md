# [VERIFICATION-004] Reporte de Verificación — Fase 4: Seguridad y JWT

Fecha de Verificación: 2026-10-25
Resultado Global: SUPERADO (PASSED)

## 1. Mapeo Criterio a Evidencia

| Criterio | Estado | Método | Evidencia / Ubicación |
| :--- | :---: | :---: | :--- |
| **AC-01: BCryptPasswordServiceAdapter** | SUPERADO | Automatizado | `nexusmarcket/src/main/java/application/adapters/security/BCryptPasswordServiceAdapter.java`: Implementa `PasswordServicePort` encapsulando `PasswordEncoder`. Verificado en `SecurityAdaptersTest.testBCryptPasswordHashingAndMatching`. |
| **AC-02: JwtServiceAdapter** | SUPERADO | Automatizado | `nexusmarcket/src/main/java/application/adapters/security/JwtServiceAdapter.java`: Implementa `JwtServicePort` con JJWT 0.12.6, firma HMAC-SHA256, inyección de secret/expiración, emisión de tokens con claims estructurados (`sub`, `ver`, `role`, `email`, `status`) y extracción tipada. Verificado en `SecurityAdaptersTest.testJwtTokenGenerationAndClaimsExtraction`. |
| **AC-03: AuthenticatedUserPrincipal** | SUPERADO | Automatizado | `nexusmarcket/src/main/java/application/adapters/security/AuthenticatedUserPrincipal.java`: Implementa `UserDetails` de Spring Security, proyecta `ROLE_<ROL>` en GrantedAuthorities y encapsula los atributos esenciales del usuario del dominio. Verificado en `SecurityAdaptersTest.testAuthenticatedUserPrincipalAuthorities`. |
| **AC-04: JwtAuthenticationFilter** | SUPERADO | Automatizado | `nexusmarcket/src/main/java/application/adapters/security/JwtAuthenticationFilter.java`: Filtro `OncePerRequestFilter` que extrae el token Bearer, valida firma criptográfica y verifica coincidencia estricta de versión (`ver == authTokenVersion`), rechazando tokens previos tras `bumpAuthTokenVersion()`. Verificado en `SecurityAdaptersTest.testJwtAuthenticationFilterValidToken` y `testJwtAuthenticationFilterRejectedOnVersionMismatch`. |
| **AC-05: SecurityConfig** | SUPERADO | Automatizado | `nexusmarcket/src/main/java/application/adapters/security/SecurityConfig.java`: Configura `SecurityFilterChain` sin sesiones (`STATELESS`), CORS habilitado con origen configurable (`FRONTEND_ORIGIN`), CSRF deshabilitado y autorización por rutas estrictas (`/api/v1/buyer/**` a `BUYER`, `/api/v1/seller/**` a `SELLER`, `/api/v1/logistics/**` a `LOGISTICS_OPERATOR`, `/api/v1/admin/**` a `ADMINISTRATOR`, `/api/v1/supervisor/**` a `SUPERVISOR`, públicas a permitAll). |
| **AC-06: Suite de Pruebas Unitarias** | SUPERADO | Automatizado | `nexusmarcket/src/test/java/application/adapters/security/SecurityAdaptersTest.java`: Suite de 6 pruebas unitarias JUnit 5 cubriendo hashing, generación/validación JWT, mapeo de roles, autenticación válida e invalidación de token por version mismatch. |
| **AC-07: Compilación y Quality Gate en Docker** | SUPERADO | Automatizado | Ejecución en contenedor: `docker compose exec -T nexusmarcket-app mvn test` con resultado `BUILD SUCCESS` (36 tests ejecutados en total, 0 fallos, 0 errores). |

## 2. Registro de Comandos de Prueba
- **Comando Ejecutado:** `docker compose exec -T nexusmarcket-app mvn test`
- **Resultado:**
  ```text
  [INFO] Running Pruebas Unitarias de Adaptadores de Seguridad y JWT
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.989 s -- in Pruebas Unitarias de Adaptadores de Seguridad y JWT
  [INFO] Running Pruebas Unitarias de Casos de Uso (Adapters / UseCases)
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.393 s -- in Pruebas Unitarias de Casos de Uso (Adapters / UseCases)
  [INFO] Running Pruebas Unitarias del Agregado Order (Dominio Puro)
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.014 s -- in Pruebas Unitarias del Agregado Order (Dominio Puro)
  [INFO] Running Pruebas Unitarias del Agregado ProductStock (Invariante de Inventario No Negativo)
  [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.013 s -- in Pruebas Unitarias del Agregado ProductStock (Invariante de Inventario No Negativo)
  [INFO] Running Pruebas Unitarias de Servicios de Dominio Puros
  [INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.026 s -- in Pruebas Unitarias de Servicios de Dominio Puros
  [INFO] Running Pruebas Unitarias del Agregado User / Buyer (Control de Sesión e Invariantes)
  [INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.010 s -- in Pruebas Unitarias del Agregado User / Buyer (Control de Sesión e Invariantes)
  [INFO] Running application.NexusmarcketApplicationTests
  [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 28.10 s -- in application.NexusmarcketApplicationTests
  [INFO] 
  [INFO] Results:
  [INFO] Tests run: 36, Failures: 0, Errors: 0, Skipped: 0
  [INFO] ------------------------------------------------------------------------
  [INFO] BUILD SUCCESS
  [INFO] Total time:  39.549 s
  [INFO] Finished at: 2026-10-25T04:08:36Z
  ```
