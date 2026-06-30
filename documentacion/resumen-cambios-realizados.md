# Resumen de cambios realizados - SmartLogix EP3

## 1. Cambios principales del proyecto

Durante la Entrega Parcial 3 el proyecto SmartLogix fue actualizado desde una base de microservicios hacia una arquitectura mas completa, incorporando integracion por API Gateway, descubrimiento de servicios, monitoreo, autenticacion JWT, pruebas unitarias y documentacion tecnica.

## 2. Cambios por rama y colaborador

| Colaborador | Rama | Cambios principales |
|---|---|---|
| Ricardo Novoa | `feature/ricardo-discovery-gateway` | Agrego `discovery-server` con Eureka, `api-gateway` con Spring Cloud Gateway, rutas hacia BFF y microservicios, configuracion de Actuator y documentacion de discovery, gateway y monitoreo. |
| Cristobal Perez | `feature/cristobal-jwt-auth` | Agrego autenticacion JWT en el BFF, endpoints de login y validacion, filtro de autenticacion, configuracion de seguridad, proteccion de rutas criticas y soporte de autenticacion en frontend. |
| Benjamin Meneses | `feature/benjamin-tests-docs` | Agrego pruebas unitarias de backend y frontend, documentacion de pruebas, guia de ejecucion, informe de resultados y ajustes a la documentacion de entrega. |

## 3. Arquitectura agregada

### Service Discovery

Se incorporo un servidor Eureka en la carpeta `discovery-server/`. Su funcion es registrar los servicios disponibles para que el API Gateway pueda resolverlos mediante nombres logicos en vez de depender de URLs fijas.

### API Gateway

Se agrego el modulo `api-gateway/` usando Spring Cloud Gateway. Este componente centraliza el acceso externo hacia:

- BFF: `/api/bff/**`
- Autenticacion: `/api/auth/**`
- Inventario: `/api/inventario/**`
- Pedidos: `/api/pedidos/**`
- Envios: `/api/envios/**`

### Monitoreo

Se configuro Spring Boot Actuator en los servicios para exponer endpoints de salud y apoyar la revision tecnica del sistema durante la ejecucion.

## 4. Seguridad JWT

Se implemento autenticacion basada en token JWT dentro del BFF.

Archivos principales:

- `bff/src/main/java/cl/duocuc/smartlogix/bff/controller/AuthController.java`
- `bff/src/main/java/cl/duocuc/smartlogix/bff/security/JwtService.java`
- `bff/src/main/java/cl/duocuc/smartlogix/bff/security/JwtAuthenticationFilter.java`
- `bff/src/main/java/cl/duocuc/smartlogix/bff/security/SecurityConfig.java`
- `frontend/src/services/auth.js`
- `frontend/src/components/ProtectedRoute.js`

La seguridad protege operaciones criticas como crear, editar o eliminar productos, crear pedidos y modificar estados de pedidos o envios.

## 5. Pruebas y cobertura

Se mantuvieron y documentaron pruebas unitarias para backend y frontend.

Backend:

- `bff/src/test/java/cl/duocuc/smartlogix/bff/BffServiceTest.java`
- `ms-inventario/src/test/java/cl/duocuc/smartlogix/inventario/InventarioServiceTest.java`
- `ms-pedidos/src/test/java/cl/duocuc/smartlogix/pedidos/PedidoServiceTest.java`
- `ms-envios/src/test/java/cl/duocuc/smartlogix/envios/EnvioServiceTest.java`

Frontend:

- `frontend/src/components/StatCard.test.js`
- `frontend/src/hooks/useDashboard.test.js`

## 6. Documentacion corregida

Se corrigio la carpeta `documentacion/` para que el contenido coincida con las ramas reales del repositorio.

Cambios realizados:

- Se reemplazaron ramas antiguas o genericas por las ramas reales de GitHub.
- Se completo el documento de seguridad JWT.
- Se actualizo el plan de branching.
- Se actualizo la evidencia de branching.
- Se actualizo el checklist de entrega.
- Se dejo documentada la responsabilidad de cada colaborador.
- Se eliminaron PDF antiguos que contenian informacion desactualizada.

## 7. Pendientes antes de entregar

- Adjuntar capturas reales de GitHub: ramas, commits y Pull Requests o merges.
- Ejecutar pruebas finales de backend y frontend.
- Adjuntar capturas de los resultados de pruebas.
- Regenerar PDF solo si el docente los exige, usando como fuente los archivos `.md` ya corregidos.
- Confirmar que `main` tenga la version final integrada del proyecto.
