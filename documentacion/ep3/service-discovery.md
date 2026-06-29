# Service Discovery - SmartLogix

SmartLogix incorpora un servidor Eureka como Service Discovery para registrar dinámicamente los microservicios disponibles en la arquitectura.

## Problema que resuelve

En una arquitectura de microservicios, las direcciones de red pueden cambiar. Service Discovery evita configurar manualmente cada URL y permite que los servicios se encuentren mediante nombres lógicos.

## Componentes registrados

- api-gateway
- bff
- ms-inventario
- ms-pedidos
- ms-envios

## Puerto

Eureka se ejecuta en:

http://localhost:8761

## Configuración

Cada microservicio usa:

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

## Ventajas

- Registro dinámico de servicios.
- Menor acoplamiento entre componentes.
- Mejor escalabilidad.
- Facilita el uso de balanceo con lb:// en API Gateway.

## Desventajas

- Agrega un componente adicional a mantener.
- Si Eureka falla, nuevos servicios no podrán registrarse hasta que vuelva a estar disponible.
