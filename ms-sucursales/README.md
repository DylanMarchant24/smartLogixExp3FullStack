# ms-sucursales
CRUD de tiendas, bodegas y puntos Click & Collect.

- Puerto: 8087
- Eureka: `ms-sucursales`
- DB local: H2 en memoria

## Endpoints
- `GET /api/sucursales`
- `GET /api/sucursales/activas`
- `GET /api/sucursales/activas?comuna=Providencia`
- `GET /api/sucursales/activas?tipo=PUNTO_RETIRO`
- `GET /api/sucursales/{id}`
- `POST /api/sucursales`
- `PUT /api/sucursales/{id}`
- `PATCH /api/sucursales/{id}/estado`
