# Seguridad JWT y flujo de autenticación – SmartLogix EP3

## Objetivo

Se implementó autenticación basada en JWT en el BFF de SmartLogix para proteger operaciones críticas del sistema logístico. El frontend inicia sesión, guarda el token y lo envía en cada solicitud protegida mediante el encabezado `Authorization`.

## Endpoints de autenticación

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Valida credenciales y genera un token JWT. |
| POST | `/api/auth/validate` | Valida si el token enviado sigue vigente. |

Usuario de prueba:

```text
usuario: admin
password: admin123