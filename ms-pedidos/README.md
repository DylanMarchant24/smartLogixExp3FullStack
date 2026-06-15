# ms-pedidos – SmartLogix

Microservicio de procesamiento de pedidos para SmartLogix.  
Valida stock contra ms-inventario, aplica Circuit Breaker y persiste pedidos con Factory Method.

## Tecnologías

- Java 17 + Spring Boot 3.2.4
- Spring Data JPA + MySQL 8
- RestTemplate (comunicación con ms-inventario)
- JaCoCo (cobertura ≥ 60%)

## Patrones aplicados

| Patrón | Clase | Descripción |
|---|---|---|
| **Repository Pattern** | `PedidoRepository` | Abstrae acceso a datos de la lógica de negocio |
| **Factory Method** | `PedidoFactory` | Crea pedidos según estado: CREADO / VALIDADO / APROBADO |
| **Circuit Breaker** | `PedidoService` | Fallback manual si ms-inventario no responde |

## Estructura

```
ms-pedidos/
├── pom.xml
└── src/
    ├── main/java/cl/duocuc/smartlogix/pedidos/
    │   ├── MsPedidosApplication.java
    │   ├── model/   (Pedido, EstadoPedido)
    │   ├── dto/     (PedidoDTO)
    │   ├── factory/ (PedidoFactory)
    │   ├── repository/ (PedidoRepository)
    │   ├── service/ (PedidoService)
    │   └── controller/ (PedidoController)
    └── test/
        └── PedidoServiceTest.java
```

## Requisitos previos

- Java 17+ | Maven 3.8+ | MySQL 8 en `localhost:3306`
- ms-inventario corriendo en `localhost:8081` (opcional, hay fallback)

## Ejecución

```bash
cd ms-pedidos
mvn spring-boot:run
# Puerto: 8082
```

## Tests

```bash
mvn test
# Cobertura JaCoCo: target/site/jacoco/index.html
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/pedidos` | Lista todos los pedidos |
| GET | `/api/pedidos/{id}` | Obtiene pedido por ID |
| GET | `/api/pedidos/estado/{estado}` | Filtra por estado |
| POST | `/api/pedidos` | Crea pedido (valida stock) |
| PATCH | `/api/pedidos/{id}/estado` | Cambia estado del pedido |

## Body de ejemplo

```json
POST /api/pedidos
{
  "skuProducto": "SKU-001",
  "cantidad": 2,
  "clienteEmail": "cliente@email.com"
}
```
