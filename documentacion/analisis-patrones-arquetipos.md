# Analisis de Patrones y Arquetipos - SmartLogix EP3

## 1. Contexto del problema

SmartLogix busca resolver problemas tipicos de una PYME de eCommerce: inventario que cambia constantemente, pedidos que dependen del stock disponible y envios que deben ser trazables. Un enfoque monolitico acoplaria todas las reglas en un solo backend y dificultaria la mantencion. Por eso se usa una arquitectura con frontend React, BFF, microservicios, API Gateway, Service Discovery, monitoreo y seguridad JWT.

## 2. Patrones de diseno implementados

### 2.1 Backend For Frontend (BFF)

Componente: `bff`.

Problema que resuelve: el frontend no debe conocer directamente todos los microservicios internos. Sin BFF, React tendria que llamar por separado a inventario, pedidos y envios, aumentando el acoplamiento.

Aplicacion: `BffController` expone rutas bajo `/api/bff/*`. `BffService` consume internamente los microservicios y devuelve respuestas adaptadas al frontend.

Aporte: reduce llamadas desde React, centraliza errores, estandariza el contrato HTTP y facilita cambiar microservicios sin romper la interfaz.

### 2.2 API Gateway

Componente: `api-gateway`.

Problema que resuelve: evita que existan multiples puntos de entrada hacia el backend y permite centralizar el acceso externo.

Aplicacion: Spring Cloud Gateway enruta solicitudes hacia el BFF y los microservicios usando nombres logicos registrados en Eureka.

Rutas principales:

- `/api/bff/**` hacia `bff-smartlogix`.
- `/api/auth/**` hacia `bff-smartlogix`.
- `/api/inventario/**` hacia `ms-inventario`.
- `/api/pedidos/**` hacia `ms-pedidos`.
- `/api/envios/**` hacia `ms-envios`.

Aporte: simplifica integracion, CORS, trazabilidad, monitoreo y posibles filtros de seguridad.

### 2.3 Repository Pattern

Componentes: `ms-inventario`, `ms-pedidos`, `ms-envios`.

Problema que resuelve: evita mezclar reglas de negocio con acceso directo a base de datos.

Aplicacion: `ProductoRepository`, `PedidoRepository` y `EnvioRepository` extienden `JpaRepository`. Los servicios usan estos repositorios como abstraccion.

Aporte: mejora mantenibilidad, permite probar servicios con mocks y separa persistencia de reglas de negocio.

### 2.4 DTO Pattern

Componentes: todos los servicios backend.

Problema que resuelve: exponer entidades JPA directamente puede filtrar campos internos y acoplar la API al modelo de persistencia.

Aplicacion: `ProductoDTO`, `PedidoDTO`, `EnvioDTO`, `DashboardDTO`, `LoginRequest`, `AuthResponse` y `ValidateResponse` definen contratos de entrada y salida.

Aporte: define datos exactos para el cliente, facilita validaciones y evita exponer detalles internos.

### 2.5 Factory Method

Componente: `ms-pedidos`, clase `PedidoFactory`.

Problema que resuelve: crear pedidos con estados y atributos iniciales repetidos en varias partes del sistema.

Aplicacion: la fabrica crea pedidos segun el flujo de negocio y centraliza el estado inicial.

Aporte: reduce duplicidad, evita inconsistencias de estado y facilita extender reglas futuras.

### 2.6 Circuit Breaker manual

Componente: `ms-pedidos`, clase `PedidoService`.

Problema que resuelve: si inventario falla, pedidos no deberia provocar una cascada de errores en todo el sistema.

Aplicacion: `PedidoService` contempla degradacion controlada cuando falla la consulta a inventario.

Aporte: mejora resiliencia y permite que el sistema responda de forma controlada ante fallas parciales.

### 2.7 Service Layer y Custom Hook en frontend

Componentes: `frontend/src/services/api.js`, `frontend/src/services/auth.js` y `frontend/src/hooks/useDashboard.js`.

Problema que resuelve: evita duplicar logica HTTP, manejo de errores y estados de carga dentro de los componentes visuales.

Aplicacion: `api.js` centraliza llamadas al backend, `auth.js` maneja autenticacion y `useDashboard` encapsula la carga del dashboard.

Aporte: componentes mas limpios, pruebas unitarias mas simples y menor acoplamiento.

### 2.8 Filtro de autenticacion JWT

Componente: `bff/src/main/java/.../security/JwtAuthenticationFilter.java`.

Problema que resuelve: las operaciones criticas no deben ejecutarse sin autenticar al usuario.

Aplicacion: el filtro lee el encabezado `Authorization`, valida el token con `JwtService` y permite o rechaza la solicitud.

Aporte: protege creacion, edicion y eliminacion de recursos importantes sin afectar las consultas publicas.

## 3. Patrones arquitectonicos

### 3.1 Microservicios

Cada dominio se separa en un servicio independiente: inventario, pedidos y envios. Esto permite mantener, probar y escalar cada parte por separado.

### 3.2 Arquitectura por capas

Cada microservicio mantiene estructura consistente: `controller`, `service`, `repository`, `model` y `dto`. Esto evita mezclar responsabilidades.

### 3.3 BFF + Microservicios

El BFF evita que el frontend dependa de todos los microservicios. Los microservicios conservan su independencia, mientras el BFF entrega una API optimizada para React.

### 3.4 API Gateway + Service Discovery

El API Gateway centraliza el acceso y Eureka permite descubrir servicios por nombre logico. Esta combinacion mejora escalabilidad y reduce configuraciones fijas entre componentes.

### 3.5 Monitoreo con Actuator

Actuator permite revisar el estado de cada servicio mediante endpoints `/actuator/health`, apoyando la operacion y defensa tecnica del proyecto.

## 4. Arquetipos Maven

Se agregaron dos arquetipos en `arquetipos-maven`:

- `smartlogix-ms-archetype`: genera microservicios con controller, service, repository, model, dto, test, `pom.xml` y configuracion de JaCoCo.
- `smartlogix-bff-archetype`: genera un BFF con controller, service, dto, RestTemplate, test, `pom.xml` y configuracion de JaCoCo.

Estos arquetipos aseguran coherencia porque cada nuevo backend nace con la misma estructura, dependencias base y convenciones de pruebas.

## 5. Relacion con ramas reales

| Rama | Aporte tecnico principal |
|---|---|
| `feature/ricardo-discovery-gateway` | API Gateway, Service Discovery y monitoreo. |
| `feature/cristobal-jwt-auth` | Seguridad JWT y flujo de autenticacion. |
| `feature/benjamin-tests-docs` | Pruebas unitarias, cobertura y documentacion. |

## 6. Justificacion de escalabilidad y mantenibilidad

La solucion es escalable porque cada microservicio puede crecer y ejecutarse de forma independiente. Es mantenible porque las reglas de negocio se ubican en servicios, el acceso a datos en repositorios, la API se define con DTO, el frontend se desacopla mediante BFF y el acceso externo se centraliza mediante API Gateway.

La incorporacion de Eureka, Actuator y JWT mejora la operacion del sistema: los servicios se descubren dinamicamente, se puede verificar su salud y las operaciones criticas quedan protegidas.

## 7. Evidencia en codigo

- BFF: `bff/src/main/java/.../controller/BffController.java` y `BffService.java`.
- API Gateway: `api-gateway/src/main/resources/application.properties`.
- Service Discovery: `discovery-server/`.
- JWT: `AuthController.java`, `JwtService.java`, `JwtAuthenticationFilter.java` y `SecurityConfig.java`.
- Repository Pattern: `ProductoRepository`, `PedidoRepository`, `EnvioRepository`.
- Factory Method: `ms-pedidos/src/main/java/.../factory/PedidoFactory.java`.
- Circuit Breaker manual: `ms-pedidos/src/main/java/.../service/PedidoService.java`.
- DTO: carpetas `dto` de cada backend y DTO de autenticacion.
- Frontend Service Layer: `frontend/src/services/api.js` y `frontend/src/services/auth.js`.
- Custom Hook: `frontend/src/hooks/useDashboard.js`.
