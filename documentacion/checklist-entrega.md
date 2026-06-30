# Checklist de Entrega - SmartLogix EP3

## 1. Documentacion tecnica

- [x] `documentacion/analisis-patrones-arquetipos.md`
- [x] `documentacion/plan-branching.md`
- [x] `documentacion/evidencia-branching.md`
- [x] `documentacion/guia-ejecucion-pruebas.md`
- [x] `documentacion/informe-pruebas.md`
- [x] `documentacion/resultados-pruebas-cobertura.md`
- [x] `documentacion/resumen-cambios-realizados.md`
- [x] `documentacion/ep3/service-discovery.md`
- [x] `documentacion/ep3/api-gateway.md`
- [x] `documentacion/ep3/monitoreo.md`
- [x] `documentacion/ep3/seguridad-jwt.md`

> Los PDF antiguos fueron eliminados de la carpeta porque contenian informacion previa y nombres de ramas que no coincidian con GitHub. Si el docente solicita PDF, se deben regenerar al final desde los `.md` ya corregidos.

## 2. Evidencias de GitHub

- [x] Ramas reales identificadas:
  - `main`
  - `develop`
  - `feature/benjamin-tests-docs`
  - `feature/cristobal-jwt-auth`
  - `feature/ricardo-discovery-gateway`
- [ ] Adjuntar captura de `Branches` en GitHub.
- [ ] Adjuntar captura de commits por rama.
- [ ] Adjuntar captura de Pull Requests o merges, si existen.
- [ ] Adjuntar evidencia del historial de Git si no se usaron Pull Requests.

## 3. Backend y arquitectura

- [x] `discovery-server/` implementado como servidor Eureka.
- [x] `api-gateway/` implementado con Spring Cloud Gateway.
- [x] `bff/` implementado como Backend For Frontend.
- [x] `ms-inventario/` implementado como microservicio de inventario.
- [x] `ms-pedidos/` implementado como microservicio de pedidos.
- [x] `ms-envios/` implementado como microservicio de envios.
- [x] Actuator configurado para monitoreo de servicios.

## 4. Seguridad JWT

- [x] Login implementado en `POST /api/auth/login`.
- [x] Validacion de token implementada en `POST /api/auth/validate`.
- [x] `JwtService` genera y valida tokens.
- [x] `JwtAuthenticationFilter` procesa el encabezado `Authorization`.
- [x] `SecurityConfig` protege operaciones criticas de inventario, pedidos y envios.
- [x] Frontend preparado para guardar y enviar el token.

## 5. Frontend

- [x] `frontend/package.json`
- [x] `frontend/src`
- [x] `frontend/public`
- [x] `frontend/README.md`
- [x] Paginas principales: Dashboard, Inventario, Pedidos, Envios y Login.
- [x] Servicio API centralizado en `frontend/src/services/api.js`.
- [x] Servicio de autenticacion en `frontend/src/services/auth.js`.
- [x] Ruta protegida mediante `ProtectedRoute.js`.

## 6. Pruebas

- [x] Pruebas backend con JUnit 5 y Mockito.
- [x] Pruebas frontend con Jest y React Testing Library.
- [x] Guia de ejecucion de pruebas documentada.
- [x] Informe de pruebas documentado.
- [ ] Ejecutar pruebas finales antes de entregar.
- [ ] Adjuntar capturas de resultados de pruebas.
- [ ] Adjuntar cobertura real si se genera reporte JaCoCo o coverage de React.

## 7. Evidencias recomendadas para capturas

| Evidencia | Que debe mostrar |
|---|---|
| Ramas GitHub | `main`, `develop` y las tres `feature/*`. |
| Commits | Autor, fecha y mensaje de commit. |
| Pull Requests | Rama origen, rama destino, estado merged o closed. |
| Eureka | Servicios registrados en `localhost:8761`. |
| Gateway | Respuesta desde `localhost:8085`. |
| Actuator | Respuesta `status: UP`. |
| Frontend | Interfaz funcionando en `localhost:3000`. |
| Login JWT | Token recibido desde `/api/auth/login`. |
| Pruebas | Resultado exitoso de Maven y npm test. |

## 8. Pendiente final

Antes de subir a Blackboard o GitHub final, revisar que no queden documentos antiguos con ramas genericas o ramas que no existan en GitHub. La documentacion debe coincidir con las ramas reales visibles en el repositorio.
