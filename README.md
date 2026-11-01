# NexusMarket - Backend & Especificación de Marketplace

NexusMarket es una plataforma digital centralizada de intermediación comercial entre compradores y empresas vendedoras. El sistema implementa una **Arquitectura Hexagonal (Ports & Adapters)** guiada por el Dominio (**Domain-Driven Design - DDD**) y una arquitectura de **Persistencia Políglota** que desacopla transacciones relacionales (MySQL 8.0 con Spring Data JPA) del catálogo flexible y carritos de compra (MongoDB 6.0 con Spring Data MongoDB).

El backend está desarrollado con **Java 17** y **Spring Boot 4.x**, con seguridad sin estado basada en **JSON Web Tokens (JWT)** y documentación viva interactiva a través de **OpenAPI 3.0 / Swagger UI**.

---

## 📋 Requisitos Previos

* **Docker Desktop** (en Windows con motor WSL2 habilitado o Linux/macOS con Docker Engine y Docker Compose v2).
* **Git** para la clonación y control de versiones.
* *(Opcional)* Java 17 y Maven 3.9+ si deseas compilar de forma nativa fuera de Docker. Todo el entorno de ejecución y compilación está completamente contenido en Docker.

---

## 🚀 Puesta en Marcha Rápida (Docker Compose)

El proyecto incluye una orquestación completa con tres servicios interconectados en una red aislada:

| Contenedor | Imagen | Puerto Expuesto | Función en la Arquitectura |
| :--- | :--- | :--- | :--- |
| `nexus-mysql` | `mysql:8.0` | `3306` | Persistencia relacional de usuarios, bodegas, inventario/kardex y pedidos (JPA). |
| `nexus-mongo` | `mongo:6.0` | `27017` | Persistencia documental de catálogo de productos, variantes, carritos y logs de auditoría. |
| `nexus-app` | Adoptium JDK 17 | `8080` | Backend Spring Boot (API REST, Seguridad JWT, Lógica de Negocio y Swagger UI). |

### 1. Iniciar el entorno completo

Abre una terminal en la raíz del repositorio (`nexusmarcket/`) y ejecuta:

```bash
docker compose up -d
```

> **Nota:** Este comando descarga las imágenes oficiales si no están presentes, construye el contenedor del aplicativo y levanta los tres servicios en segundo plano (`-d`). La aplicación esperará automáticamente a que MySQL y MongoDB superen sus chequeos de salud (*healthchecks*) antes de iniciar Spring Boot.

### 2. Monitorear el arranque en tiempo real

Para seguir la salida de logs del aplicativo Spring Boot:

```bash
docker compose logs -f nexusmarcket-app
```

Cuando observes la siguiente línea, la plataforma estará lista para recibir peticiones:
```text
INFO --- application.NexusmarcketApplication : Started NexusmarcketApplication in XX.XXX seconds
```
*(Presiona `Ctrl + C` para salir de la visualización de logs; el servicio continuará ejecutándose en segundo plano).*

---

## 🌐 Documentación Interactiva y Pruebas (Swagger UI)

Una vez iniciada la aplicación, accede desde cualquier navegador a la interfaz gráfica interactiva de Swagger UI:

* **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) *(o `http://localhost:8080/swagger-ui/index.html`)*
* **OpenAPI 3.0 JSON Specification:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Flujo de Autenticación y Consumo en Swagger:

1. **Obtener Token JWT:**
   * Despliega la sección **`01. Public & Auth`**.
   * Regístrate como comprador en `POST /api/v1/auth/register-buyer` o inicia sesión con una cuenta existente en `POST /api/v1/auth/login`.
   * Copia el valor de la clave `"token"` devuelta en la respuesta JSON.
2. **Autorizar la sesión en Swagger:**
   * En la parte superior derecha de Swagger UI, haz clic en el botón verde **Authorize** 🔓.
   * En el campo `Value`, pega el token JWT (no es necesario anteponer la palabra `Bearer`).
   * Haz clic en **Authorize** y luego en **Close**. El candado cambiará a cerrado 🔒.
3. **Consumir Endpoints Protegidos:**
   * Ahora todas las peticiones que ejecutes a los controladores de rol incluirán automáticamente la cabecera `Authorization: Bearer <token>`.

---

## 🛠️ Comandos de Gestión con Docker

A continuación se resumen los comandos más frecuentes para la operación y ciclo de vida del proyecto:

| Operación | Comando | Descripción |
| :--- | :--- | :--- |
| **Iniciar servicios** | `docker compose up -d` | Levanta MySQL, MongoDB y Spring Boot en segundo plano. |
| **Ver estado de servicios** | `docker compose ps` | Muestra el estado de salud y puertos de los contenedores. |
| **Ver logs del backend** | `docker compose logs -f nexusmarcket-app` | Inspecciona logs en vivo de Spring Boot. |
| **Ver logs de bases de datos** | `docker compose logs -f mysql-db mongo-db` | Inspecciona logs de MySQL y MongoDB. |
| **Reiniciar la aplicación** | `docker compose restart nexusmarcket-app` | Reinicia únicamente el contenedor de la aplicación. |
| **Reconstruir la aplicación** | `docker compose up -d --build nexusmarcket-app` | Vuelve a compilar la imagen Docker tras cambios en configuración. |
| **Detener servicios** | `docker compose down` | Apaga los contenedores preservando los datos de las bases de datos. |
| **Limpieza total (Reset BD)** | `docker compose down -v` | Detiene y **elimina los volúmenes** (`mysql_data`, `mongo_data`). Ideal para restablecer datos desde cero. |
| **Monitoreo de recursos** | `docker stats` | Visualiza el consumo de CPU y memoria de los contenedores en tiempo real. |

---

## 🧪 Ejecución de Pruebas Automatizadas

No requieres tener Maven ni Java instalados localmente en tu sistema operativo host; las pruebas se ejecutan directamente dentro del contenedor mediante `docker compose exec`:

### Ejecutar toda la suite de pruebas (Unitarias, Integración y MockMvc):
```bash
docker compose exec -T nexusmarcket-app mvn test
```

### Ejecutar una clase de pruebas específica:
```bash
# Pruebas de controladores REST y contratos OpenAPI:
docker compose exec -T nexusmarcket-app mvn test -Dtest=RestControllersTest

# Pruebas unitarias de agregados del dominio:
docker compose exec -T nexusmarcket-app mvn test -Dtest=BuyerTest,UserTest,OrderTest,ProductStockTest
```

### Empaquetado y verificación limpia del artefacto JAR:
```bash
docker compose exec -T nexusmarcket-app mvn clean package -DskipTests
```

---

## 🏛️ Arquitectura de Controladores y Endpoints

Los endpoints están estructurados bajo el Principio de Segregación de Interfaces (ISP) según el rol de negocio:

* **`01. Public & Auth` (`/api/v1/auth/**`, `/api/v1/public/**`):** Login global, auto-registro de compradores y consulta pública de catálogo.
* **`02. Buyer Role` (`/api/v1/buyer/**`):** Perfil del cliente, gestión de carrito de compras, checkout y consulta de órdenes propias.
* **`03. Seller Role` (`/api/v1/seller/**`):** Publicación de productos físicos/digitales y gestión de catálogo propio.
* **`04. Logistics Role` (`/api/v1/logistics/**`):** Despacho de pedidos, consulta de bodegas y movimientos de inventario kardex.
* **`05. Administrator Role` (`/api/v1/admin/**`):** Gobernanza del sistema (OBJ-01), alta y consulta de usuarios (`/users`), cambio de estado de cuenta (`/users/{id}/status`), alta de vendedores (`/sellers`) y bodegas (`/warehouses`).
* **`06. Supervisor Role` (`/api/v1/supervisor/**`):** Seguimiento operativo de solo lectura, consulta de órdenes y auditoría transaccional (`/audit-logs`).

---

## ⚙️ Variables de Entorno y Configuración

El archivo [`docker-compose.yml`](docker-compose.yml) define las variables de entorno principales con valores por defecto para desarrollo:

| Variable | Valor por Defecto | Propósito |
| :--- | :--- | :--- |
| `SERVER_PORT` | `8080` | Puerto HTTP del servidor Spring Boot Tomcat. |
| `DB_URL` | `jdbc:mysql://mysql-db:3306/nexus_db` | Cadena JDBC de conexión a MySQL. |
| `DB_USERNAME` | `nexus_app` | Usuario de base de datos MySQL. |
| `DB_PASSWORD` | `nexus_app_password` | Contraseña de base de datos MySQL. |
| `MONGODB_URI` | `mongodb://mongo-db:27017/nexus_catalog_db` | Cadena de conexión para MongoDB. |
| `JWT_SECRET` | `TDEATDEATDEATDEATDEATDEATDEATDEA` | Clave simétrica HMAC-SHA256 para firma y validación de tokens. |
| `JWT_EXPIRATION_MS` | `3600000` (1 hora) | Tiempo de expiración del token JWT emitido. |
| `FRONTEND_ORIGIN` | `http://localhost:5173` | Origen permitido para políticas CORS (Vite/React). |
