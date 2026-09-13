# ms-pagos
Microservicio de pagos simulados de SmartLogix.

- Puerto: 8086
- Eureka: `ms-pagos`
- DB local: H2 en memoria
- Token de prueba aprobado: `TEST-APROBADA`
- Token de prueba rechazado: `TEST-RECHAZADA`

## Endpoints
- `POST /api/pagos/procesar`
- `GET /api/pagos`
- `GET /api/pagos/pedido/{pedidoId}`

No almacena números completos de tarjeta; solo token de prueba y últimos 4 dígitos.
