# Seguridad JWT y flujo de autenticacion - SmartLogix EP3

## 1. Objetivo

SmartLogix incorpora autenticacion basada en JWT en el BFF para proteger operaciones criticas del sistema logistico. El frontend realiza login, recibe un token y lo envia en las solicitudes protegidas mediante el encabezado `Authorization`.

Esta funcionalidad corresponde a la rama:

```text
feature/cristobal-jwt-auth
```

## 2. Componentes principales

| Componente | Archivo | Funcion |
|---|---|---|
| Controlador de autenticacion | `AuthController.java` | Expone login y validacion de token. |
| Servicio JWT | `JwtService.java` | Genera, valida y lee informacion del token. |
| Filtro de seguridad | `JwtAuthenticationFilter.java` | Intercepta solicitudes con encabezado `Authorization`. |
| Configuracion de seguridad | `SecurityConfig.java` | Define rutas publicas y rutas protegidas. |
| DTO de login | `LoginRequest.java` | Recibe usuario y password. |
| DTO de respuesta | `AuthResponse.java` | Devuelve token, tipo y expiracion. |
| DTO de validacion | `ValidateResponse.java` | Indica si el token sigue vigente. |

## 3. Endpoints de autenticacion

| Metodo | Endpoint | Descripcion | Acceso |
|---|---|---|---|
| POST | `/api/auth/login` | Valida credenciales y genera token JWT. | Publico |
| POST | `/api/auth/validate` | Valida si el token enviado sigue vigente. | Publico |

Usuario de prueba:

```text
usuario: admin
password: admin123
```

## 4. Flujo de autenticacion

1. El usuario ingresa sus credenciales en el frontend.
2. El frontend envia la solicitud a `POST /api/auth/login`.
3. El BFF valida el usuario de prueba.
4. Si las credenciales son correctas, `JwtService` genera un token.
5. El frontend guarda el token.
6. En operaciones protegidas, el frontend envia:

```text
Authorization: Bearer <token>
```

7. `JwtAuthenticationFilter` valida el token antes de permitir la operacion.

## 5. Rutas protegidas

Las consultas generales quedan publicas para permitir visualizar dashboard y listados. Las operaciones criticas requieren token JWT.

| Metodo | Ruta protegida | Motivo |
|---|---|---|
| POST | `/api/bff/inventario` | Crear producto modifica datos del sistema. |
| PUT | `/api/bff/inventario/{id}` | Actualizar producto modifica inventario. |
| DELETE | `/api/bff/inventario/{id}` | Eliminar producto es una operacion critica. |
| POST | `/api/bff/pedidos` | Crear pedido afecta stock y flujo de negocio. |
| PATCH | `/api/bff/pedidos/{id}/estado` | Cambiar estado altera el ciclo de pedido. |
| POST | `/api/bff/envios` | Crear envio modifica trazabilidad logistica. |
| PATCH | `/api/bff/envios/{id}/estado` | Cambiar estado altera seguimiento del envio. |

## 6. Configuracion relevante

En `bff/src/main/resources/application.properties` se configuran los valores JWT:

```properties
jwt.secret=smartlogix-jwt-secret-key-2026-fullstack-ep3
jwt.expiration-minutes=60
```

Tambien se mantiene el registro del BFF en Eureka:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

## 7. Ejemplo de prueba manual

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Respuesta esperada:

```json
{
  "token": "TOKEN_GENERADO",
  "type": "Bearer",
  "username": "admin",
  "expirationMinutes": 60
}
```

### Validar token

```bash
curl -X POST http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer TOKEN_GENERADO"
```

Respuesta esperada:

```json
{
  "valid": true,
  "username": "admin"
}
```

## 8. Aporte al proyecto

La autenticacion JWT mejora la seguridad del sistema porque separa operaciones publicas de operaciones criticas. Esto permite que el usuario pueda consultar informacion general, pero exige autenticacion para modificar inventario, pedidos o envios.
