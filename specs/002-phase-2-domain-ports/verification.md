# [VERIFICATION-002] Reporte de Verificación — Fase 2: Capa de Dominio Pura y Puertos

Fecha de Verificación: 2026-10-20
Resultado Global: SUPERADO (PASSED)

## 1. Mapeo Criterio a Evidencia

| Criterio | Estado | Método | Evidencia / Ubicación |
| :--- | :---: | :---: | :--- |
| **AC-01: Excepciones de Dominio** | SUPERADO | Inspección Estática | `nexusmarcket/src/main/java/application/domain/exceptions/`: `DomainException`, `NegativeStockException`, `InvalidOrderStateException`, `InvalidUserStatusException`, `UnauthorizedRoleOperationException`, `EntityNotFoundException`, `InvalidCredentialsException`, `ProductNotAvailableException`. Cero imports externos. |
| **AC-02: Entidades Ricas** | SUPERADO | Pruebas Unitarias | `nexusmarcket/src/main/java/application/domain/models/`: `User` con `bumpAuthTokenVersion()` y `changePassword()`; `ProductStock` protegiendo la regla de inventario nunca negativo; `Order` controlando transiciones de estado, inmutabilidad de órdenes entregadas/canceladas y recálculo automático de subtotales, IVA y total; `Buyer`, `Seller`, `Product`, `ShoppingCart`, etc. |
| **AC-03: Value Objects Inmutables** | SUPERADO | Pruebas Unitarias | `nexusmarcket/src/main/java/application/domain/valueobjects/`: `OrderLine` y `CartItem` inmutables, cálculo de subtotal derivado de cantidad y precio, igualdad semántica por atributos. |
| **AC-04: Puertos de Salida** | SUPERADO | Inspección Estática | `nexusmarcket/src/main/java/application/domain/ports/out/`: 12 interfaces puras desacopladas de frameworks para persistencia políglota (MySQL y MongoDB) y utilidades de seguridad (`JwtServicePort`, `PasswordServicePort`). |
| **AC-05: Puertos de Entrada Segregados** | SUPERADO | Inspección Estática | `nexusmarcket/src/main/java/application/domain/ports/in/`: Segregación por roles de actor: `PublicAccessPort`, `BuyerPort`, `SellerPort`, `LogisticsOperatorPort`, `AdministratorPort`, `SupervisorPort`. |
| **AC-06: Servicios de Dominio Puros** | SUPERADO | Pruebas Unitarias | `nexusmarcket/src/main/java/application/domain/services/`: Clases atómicas en `auth`, `order`, `inventory` y `catalog`. Sin anotaciones `@Service` de Spring en el dominio, garantizando pureza de arquitectura hexagonal. |
| **AC-07: Validación Automatizada en Contenedor** | SUPERADO | Ejecución de Pruebas | Comando ejecutado: `docker compose exec -T nexusmarcket-app mvn test`. 24 pruebas ejecutadas, 0 fallos, 0 errores, tiempo de compilación y ejecución completado con `BUILD SUCCESS` (código de salida 0). |

## 2. Registro de Comandos de Prueba
- **Comando:** `docker compose exec -T nexusmarcket-app mvn test`
- **Resultado:**
  ```text
  [INFO] Running Pruebas Unitarias del Agregado Order (Dominio Puro)
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running Pruebas Unitarias del Agregado ProductStock (Invariante de Inventario No Negativo)
  [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running Pruebas Unitarias de Servicios de Dominio Puros
  [INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running Pruebas Unitarias del Agregado User / Buyer (Control de Sesión e Invariantes)
  [INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
  [INFO] Running application.NexusmarcketApplicationTests
  [INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
  [INFO] 
  [INFO] Results:
  [INFO] Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
  [INFO] BUILD SUCCESS
  ```
