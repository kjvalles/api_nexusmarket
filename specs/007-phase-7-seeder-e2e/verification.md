# [VERIFICATION-007] Informe de Verificación de Fase 7: Carga Inicial de Datos Semilla y Pruebas End-to-End

Fecha: 2026-11-05  
Estado: APROBADO (100% de criterios cumplidos)  
Entorno: Docker (Linux x86_64, OpenJDK 17 Adoptium, MySQL 8.0, MongoDB 6.0)

---

## 1. Resumen de Ejecución de Pruebas

Se ejecutó la suite completa de pruebas unitarias y de integración dentro del contenedor Docker `nexusmarcket-app`:

```bash
docker compose exec -T nexusmarcket-app mvn test
```

### Resultados de Maven Surefire:
- **Pruebas Unitarias de Adaptadores de Persistencia Políglota**: 10 tests, 0 fallas, 0 errores.
- **RestControllersTest**: 41 tests, 0 fallas, 0 errores.
- **JwtTokenProviderTest**: 7 tests, 0 fallas, 0 errores.
- **EndToEndPlatformIntegrationTest**: 4 tests, 0 fallas, 0 errores.
  - `e2e_completeCommercialCycle_shouldSucceed()`: Verificado flujo completo (catálogo, carrito, inventario, auditoría).
  - `e2e_authenticationAllRoles_shouldSucceed()`: Autenticación exitosa de los 5 roles (`admin`, `supervisor`, `logistica`, `vendedor`, `comprador`).
  - `e2e_warehouseInventoryConsistency_shouldMatch()`: Coherencia de existencias físicas y reservas en MySQL.
  - `e2e_seededOrdersState_shouldBeValid()`: Validación de estados y líneas congeladas de órdenes sembradas.
- **NexusmarcketApplicationTests**: 1 test, 0 fallas, 0 errores.
- **Total**: 72 tests ejecutados, 0 fallas, 0 errores, 0 omitidos. **BUILD SUCCESS**.

---

## 2. Verificación de Criterios de Aceptación

| Criterio | Descripción | Estado | Evidencia |
| :--- | :--- | :---: | :--- |
| **AC-01** | Sembrado de usuarios para los 5 roles con clave `Password123*` | APROBADO | Usuarios creados con BCrypt; login verificado en live HTTP y tests. |
| **AC-02** | Sembrado de bodegas `wh-central-01` y `wh-seller-01` en MySQL | APROBADO | Bodegas persistidas con tipo MARKETPLACE y SELLER; validadas en E2E. |
| **AC-03** | Sembrado de catálogo políglota en MongoDB con variantes | APROBADO | 5 productos (3 físicos con variantes, 1 digital, 1 suspendido) en MongoDB. |
| **AC-04** | Sembrado de stock y movimientos kardex en MySQL | APROBADO | Balances en `product_stocks` y entradas en `inventory_movements` registrados. |
| **AC-05** | Sembrado de carrito activo en Mongo y órdenes de prueba en MySQL | APROBADO | Carrito comprador activo; 3 órdenes (`DELIVERED`, `SHIPPED`, `PENDING_PAYMENT`) funcionales. |
| **AC-06** | Sembrado de registros de auditoría en MongoDB | APROBADO | 3 registros iniciales trazados en colección `audit_logs`. |
| **AC-07** | Verificación End-to-End y 0 fallas en Docker | APROBADO | 72/72 pruebas superadas con éxito en entorno de contenedores. |

---

## 3. Verificación en Vivo de la Plataforma

Tras reiniciar el contenedor con `docker compose restart nexusmarcket-app`, la aplicación cargó los datos semilla y autentica correctamente vía `POST /api/v1/auth/login`:

- **Administrador**: `admin@nexusmarcket.com` -> HTTP 200 (Token JWT generado, Rol ADMINISTRATOR)
- **Supervisor**: `supervisor@nexusmarcket.com` -> HTTP 200 (Token JWT generado, Rol SUPERVISOR)
- **Operador Logístico**: `logistica@nexusmarcket.com` -> HTTP 200 (Token JWT generado, Rol LOGISTICS_OPERATOR)
- **Vendedor**: `vendedor@nexusmarcket.com` -> HTTP 200 (Token JWT generado, Rol SELLER)
- **Comprador**: `comprador@nexusmarcket.com` -> HTTP 200 (Token JWT generado, Rol BUYER)
