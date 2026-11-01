# [SPEC-004] Fase 4: Seguridad y JWT (Adapters / Security)

Fecha: 2026-10-25
Estado: ACTIVO
Tipo: feature

## 0. Fase de Clarificación y Supuestos
- **Alineación Arquitectónica:** Se implementan los adaptadores de seguridad en `application.adapters.security.*` siguiendo la arquitectura hexagonal validada en el repositorio guía (`construccion_de_software_2_2026_2/bank`).
- **Separación de Responsabilidades:**
  - El dominio define los contratos de salida: [`PasswordServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/PasswordServicePort.java) y [`JwtServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/JwtServicePort.java).
  - La capa `adapters/security/` provee las implementaciones concretas con Spring Security y JJWT 0.12.6 sin contaminar el dominio.
- **Estrategia de Tokens e Invalidación de Sesiones:**
  - JWT Stateless: no se guardan tokens en base de datos.
  - Revocación activa: el token incluye el claim `ver` (`authTokenVersion`). El filtro valida que este claim coincida con la versión del usuario. Cuando el usuario cambia contraseña o el administrador modifica su estado, `User.bumpAuthTokenVersion()` incrementa el contador, invalidando de inmediato cualquier token previamente emitido sin requerir blacklist en caché.
  - Privacidad (PII): Los tokens no incluyen contraseñas, saldos ni información sensible innecesaria; solo `sub` (userId), `email`, `role` y `ver`.
- **Control de Acceso por Roles (RBAC):**
  - Rutas públicas: `/api/v1/auth/**`, `/api/v1/public/**`, `/actuator/**`.
  - Rutas protegidas por rol con prefijo `ROLE_`:
    - Comprador: `/api/v1/buyer/**` -> `ROLE_BUYER`
    - Vendedor: `/api/v1/seller/**` -> `ROLE_SELLER`
    - Operador Logístico: `/api/v1/logistics/**` -> `ROLE_LOGISTICS_OPERATOR`
    - Administrador: `/api/v1/admin/**` -> `ROLE_ADMINISTRATOR`
    - Supervisor: `/api/v1/supervisor/**` -> `ROLE_SUPERVISOR`

## 1. Need and scope
- **Necesidad:** En la Fase 2 y 3 se definieron los modelos y casos de uso, pero la autenticación y la autorización dependen de servicios de seguridad reales. Se requiere una implementación de hashing de contraseñas con BCrypt, emisión de tokens JWT seguros y un filtro de Spring Security que proteja las rutas HTTP según el rol del usuario autenticado.
- **Alcance (Scope):**
  - Implementación de `BCryptPasswordServiceAdapter` implementando [`PasswordServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/PasswordServicePort.java).
  - Implementación de `JwtServiceAdapter` implementando [`JwtServicePort`](../../nexusmarcket/src/main/java/application/domain/ports/out/JwtServicePort.java) utilizando JJWT 0.12.6.
  - Implementación de `AuthenticatedUserPrincipal` implementando `UserDetails` de Spring Security.
  - Implementación de `JwtAuthenticationFilter` para interceptar requests, validar firmas y verificar vigencia de versión de token.
  - Implementación de `SecurityConfig` configurando la cadena de filtros `SecurityFilterChain`, CORS y políticas stateless.
  - Suite de pruebas unitarias de seguridad en `src/test/java/application/adapters/security/`.
  - Verificación del quality gate automatizado en Docker (`docker compose exec -T nexusmarcket-app mvn test`).

## 2. Acceptance Criteria and Non-Goals
### Criterios de Aceptación (Acceptance Criteria)
- **AC-01:** Implementación de `BCryptPasswordServiceAdapter` en `application.adapters.security` implementando `PasswordServicePort`, permitiendo encriptar contraseñas en texto plano con salt y verificar coincidencias de forma segura.
- **AC-02:** Implementación de `JwtServiceAdapter` en `application.adapters.security` implementando `JwtServicePort` con JJWT 0.12.6, parametrizado con clave secreta y tiempo de expiración desde `application.properties`, emitiendo tokens firmados con claims (`sub`, `email`, `role`, `ver`) y extrayendo sus valores.
- **AC-03:** Implementación de `JwtAuthenticationFilter` en `application.adapters.security` interceptando peticiones HTTP con header `Authorization: Bearer <token>`, validando la firma criptográfica, comprobando que el claim `ver` coincida con el `authTokenVersion` del usuario e inyectando la autenticación en el `SecurityContextHolder`.
- **AC-04:** Implementación de `AuthenticatedUserPrincipal` en `application.adapters.security` encapsulando al usuario de dominio y proveyendo la autoridad `ROLE_<ROLE>` para control granular de permisos en Spring Security.
- **AC-05:** Implementación de `SecurityConfig` en `application.adapters.security` configurando `SecurityFilterChain` en modo `STATELESS`, deshabilitando CSRF, configurando CORS con `FRONTEND_ORIGIN` y restringiendo rutas estrictamente por rol (`/api/v1/buyer/**`, `/api/v1/seller/**`, `/api/v1/logistics/**`, `/api/v1/admin/**`, `/api/v1/supervisor/**`).
- **AC-06:** Suite de pruebas unitarias con JUnit 5 en `src/test/java/application/adapters/security/` validando cifrado, generación de tokens, validación de expiración, control de versión de tokens y rechazo de tokens inválidos.
- **AC-07:** Verificación exitosa en Docker Compose mediante `docker compose exec -T nexusmarcket-app mvn test` con resultado `BUILD SUCCESS` (0 errores, 0 fallos).

### Fuera de Alcance (Non-Goals)
- No incluye adaptadores de persistencia JPA ni MongoDB (Fase 5).
- No incluye controladores REST ni endpoints de transporte HTTP `/api/v1/**` (Fase 6).
- No incluye proveedores OAuth2 externos (Google, Facebook).

## 3. Domain applicability
| Security & Compliance Control | Required? | Technical Enforcement |
| :--- | :---: | :--- |
| **¿Accede o procesa datos sensibles (PII)?** | SÍ | Las contraseñas se almacenan únicamente como hashes BCrypt. Los tokens JWT se firman con HMAC-SHA256 y no exponen contraseñas ni datos sensibles innecesarios. |
| **¿Expone datos cross-tenant o cross-role?** | SÍ | Se protegen los endpoints a nivel de `SecurityFilterChain` asegurando que ningún usuario acceda a rutas de roles ajenos a su asignación única. |
| **¿Requiere audit logging?** | SÍ | Los rechazos por token inválido o expirado se registran a nivel de depuración sin filtrar datos confidenciales. |
| **Control de Caducidad e Invalidación** | SÍ | Mecanismo de doble validación: expiración temporal por `exp` e invalidación inmediata por versión de token (`authTokenVersion`). |

## 4. Experiencia y límites
- **Interfaces de usuario:** N/A justificado. La seguridad opera a nivel de filtros HTTP de backend.
- **Manejo de Errores de Autenticación:** Peticiones sin token o con token inválido/expirado/revocado retornan códigos de error HTTP 401 Unauthorized o 403 Forbidden según corresponda.

## 5. Traceability
- **AC-01** -> Plan Sección 2.1 -> `TASK-01` en `tasks.md`
- **AC-02** -> Plan Sección 2.2 -> `TASK-02` en `tasks.md`
- **AC-03** -> Plan Sección 2.3 -> `TASK-03` en `tasks.md`
- **AC-04** -> Plan Sección 2.4 -> `TASK-04` en `tasks.md`
- **AC-05** -> Plan Sección 2.5 -> `TASK-05` en `tasks.md`
- **AC-06** -> Plan Sección 2.6 -> `TASK-06` en `tasks.md`
- **AC-07** -> Plan Sección 4 -> `TASK-07` en `tasks.md`
