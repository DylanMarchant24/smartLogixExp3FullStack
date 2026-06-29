# API Gateway - SmartLogix

SmartLogix incorpora un API Gateway usando Spring Cloud Gateway. Su función es actuar como punto de entrada principal para las solicitudes externas y enrutar las peticiones hacia el BFF y los microservicios.

## Problema que resuelve

En una arquitectura de microservicios, el frontend no debería conocer directamente todas las URLs internas de los servicios. El API Gateway centraliza el acceso y permite que las solicitudes ingresen por un único punto.

## Puerto

El API Gateway se ejecuta en:

http://localhost:8085

## Rutas principales

| Ruta | Servicio destino | Descripción |
|---|---|---|
| /api/bff/** | bff | Enruta solicitudes hacia el Backend For Frontend |
| /api/inventario/** | ms-inventario | Enruta solicitudes directas al microservicio de inventario |
| /api/pedidos/** | ms-pedidos | Enruta solicitudes directas al microservicio de pedidos |
| /api/envios/** | ms-envios | Enruta solicitudes directas al microservicio de envíos |

## Relación con Eureka

El Gateway utiliza nombres lógicos registrados en Eureka:

- lb://bff
- lb://ms-inventario
- lb://ms-pedidos
- lb://ms-envios

Esto permite que el Gateway no dependa de direcciones fijas, sino de los servicios registrados dinámicamente en el Discovery Server.

## Ventajas

- Centraliza el punto de entrada del sistema.
- Oculta la topología interna de microservicios.
- Facilita la escalabilidad.
- Permite aplicar filtros transversales como seguridad, logging, monitoreo o control de errores.
- Mejora la mantenibilidad del frontend, ya que no necesita conocer cada microservicio.
