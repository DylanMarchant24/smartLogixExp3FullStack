# bff – Backend For Frontend – SmartLogix

Capa intermedia que agrega y adapta datos de los tres microservicios (inventario, pedidos, envíos) en respuestas optimizadas para el frontend React. Actúa también como API Gateway.

## Tecnologías

- Java 17 + Spring Boot 3.2.4
- RestTemplate (comunicación con microservicios)
- Sin base de datos propia (stateless)

## Patrón aplicado: Backend For Frontend (BFF)

| Problema | Solución |
|---|---|
| El frontend necesitaría 3 llamadas para cargar el dashboard | El BFF las hace internamente y retorna una sola respuesta |
| Cambios en la API de microservicios afectarían al frontend | El BFF actúa como contrato estable hacia el frontend |
| El frontend no debe conocer la topología interna | El BFF oculta la red interna de microservicios |

## Estructura

```
bff/
├── pom.xml
└── src/main/java/cl/duocuc/smartlogix/bff/
    ├── BffApplication.java
    ├── dto/   DashboardDTO.java
    ├── service/ BffService.java
    └── controller/ BffController.java
```

## Puertos del sistema completo

| Servicio | Puerto |
|---|---|
| **BFF** (punto de entrada frontend) | **8080** |
| ms-inventario | 8081 |
| ms-pedidos | 8082 |
| ms-envios | 8083 |

## Ejecución

```bash
# Iniciar primero los microservicios, luego el BFF
cd bff
mvn spring-boot:run
```

## Endpoints del BFF

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/bff/dashboard` | Dashboard agregado (inventario + pedidos + envíos) |
| GET | `/api/bff/inventario` | Lista productos vía ms-inventario |
| POST | `/api/bff/inventario` | Crea producto vía ms-inventario |
| PUT | `/api/bff/inventario/{id}` | Actualiza producto vía ms-inventario |
| DELETE | `/api/bff/inventario/{id}` | Elimina producto vía ms-inventario |
| GET | `/api/bff/pedidos` | Lista pedidos vía ms-pedidos |
| POST | `/api/bff/pedidos` | Crea pedido vía ms-pedidos |
| PATCH | `/api/bff/pedidos/{id}/estado` | Cambia estado de pedido vía ms-pedidos |
| GET | `/api/bff/envios` | Lista envíos vía ms-envios |
| POST | `/api/bff/envios` | Crea envío vía ms-envios |
| PATCH | `/api/bff/envios/{id}/estado` | Actualiza estado de envío vía ms-envios |

## Respuesta de ejemplo: GET /api/bff/dashboard

```json
{
  "resumen": {
    "totalProductos": 12,
    "totalPedidos": 45,
    "pedidosAprobados": 38,
    "pedidosPendientes": 7,
    "totalEnvios": 30,
    "enviosPendientes": 5
  },
  "productos": [...],
  "pedidos": [...],
  "envios": [...]
}
```
