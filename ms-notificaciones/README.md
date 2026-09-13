# ms-notificaciones

Microservicio del sistema SmartLogix encargado del historial y envio simulado de correos/alertas.

## Responsabilidad

- Registrar notificaciones (tipo EMAIL o ALERTA) dirigidas a un destinatario.
- Simular el envio (no se conecta a un servidor SMTP real) y dejar registro del resultado.
- Exponer el historial completo, filtrado por destinatario o por estado.
- Permitir reintentar el envio de una notificacion fallida.

## Endpoints principales

| Metodo | Ruta                                   | Descripcion                              |
|--------|-----------------------------------------|-------------------------------------------|
| POST   | /notificaciones                         | Crea y envia (simulado) una notificacion  |
| GET    | /notificaciones                         | Historial completo, mas reciente primero  |
| GET    | /notificaciones/{id}                    | Detalle de una notificacion               |
| GET    | /notificaciones/destinatario/{valor}    | Historial filtrado por destinatario       |
| GET    | /notificaciones/estado/{ESTADO}         | Historial filtrado por estado             |
| POST   | /notificaciones/{id}/reenviar           | Reintenta el envio de una notificacion    |

## Acceso via API Gateway

Todas las rutas quedan expuestas a traves del gateway bajo el prefijo `/api/notificaciones/**`.

## Configuracion

Puerto: `8090`. Se registra en Eureka como `ms-notificaciones`. Requiere una base de datos MySQL
`smartlogix_notificaciones` (se crea automaticamente si no existe segun la config de `application.properties`).
