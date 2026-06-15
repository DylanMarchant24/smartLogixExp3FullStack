# Frontend SmartLogix - React + NPM

Aplicación web para operar el dashboard logístico, inventario, pedidos y envíos de SmartLogix.

## Rol en la arquitectura

El frontend aplica el patrón **Service Layer** mediante `src/services/api.js`: todos los llamados HTTP pasan por el BFF (`/api/bff`) y nunca se consume directamente un microservicio. Esto reduce acoplamiento y permite que el contrato del frontend sea estable aunque cambie la estructura interna del backend.

También se implementa el patrón **Custom Hook** en `src/hooks/useDashboard.js`, encapsulando la carga de datos, estados de `loading`, errores y refresco del dashboard. Los componentes se mantienen enfocados en renderizar.

## Requisitos

- Node.js 18+
- NPM 9+
- BFF ejecutándose en `http://localhost:8080`

## Instalación

```bash
cd frontend
npm install
```

## Ejecución local

```bash
npm start
```

La aplicación queda disponible en:

```text
http://localhost:3000
```

El proxy del `package.json` redirige las llamadas `/api/bff` hacia `http://localhost:8080`.

## Scripts disponibles

| Script | Uso |
|---|---|
| `npm start` | Ejecuta React en modo desarrollo |
| `npm run build` | Genera build productivo |
| `npm test` | Ejecuta pruebas unitarias sin modo watch |
| `npm run test:coverage` | Ejecuta pruebas y genera cobertura |

## Estructura

```text
frontend/
├── package.json
├── public/
└── src/
    ├── components/     # Layout, StatCard, Spinner, Toast
    ├── hooks/          # useDashboard, useToast
    ├── pages/          # Dashboard, Inventario, Pedidos, Envios
    ├── services/       # api.js: capa de servicio contra el BFF
    ├── App.js
    └── index.js
```

## Pruebas y cobertura

```bash
cd frontend
npm run test:coverage
```

El reporte queda en:

```text
frontend/coverage/lcov-report/index.html
```

## Endpoints usados desde `src/services/api.js`

| Función frontend | Endpoint BFF |
|---|---|
| `getDashboard()` | `GET /api/bff/dashboard` |
| `getInventario()` | `GET /api/bff/inventario` |
| `crearProducto(data)` | `POST /api/bff/inventario` |
| `actualizarProducto(id, data)` | `PUT /api/bff/inventario/{id}` |
| `eliminarProducto(id)` | `DELETE /api/bff/inventario/{id}` |
| `getPedidos()` | `GET /api/bff/pedidos` |
| `crearPedido(data)` | `POST /api/bff/pedidos` |
| `cambiarEstado(id, estado)` | `PATCH /api/bff/pedidos/{id}/estado` |
| `getEnvios()` | `GET /api/bff/envios` |
| `crearEnvio(data)` | `POST /api/bff/envios` |
| `actualizarEnvio(id, estado)` | `PATCH /api/bff/envios/{id}/estado` |
