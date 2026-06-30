# API Gateway - SmartLogix EP3

## 1. Objetivo

SmartLogix incorpora un API Gateway usando Spring Cloud Gateway. Su funcion es actuar como punto de entrada principal para las solicitudes externas y enrutar las peticiones hacia el BFF y los microservicios.

Esta funcionalidad corresponde principalmente a la rama:

```text
feature/ricardo-discovery-gateway
```

## 2. Problema que resuelve

En una arquitectura de microservicios, el frontend no deberia conocer directamente todas las URL internas de los servicios. El API Gateway centraliza el acceso y permite que las solicitudes ingresen por un unico punto.

Esto mejora la mantenibilidad porque el frontend puede comunicarse con una entrada comun y los cambios internos de puertos o nombres de servicios quedan encapsulados en el Gateway.

## 3. Puerto

El API Gateway se ejecuta en:

```text
http://localhost:8085
```

## 4. Rutas principales

| Ruta | Servicio destino | Descripcion |
|---|---|---|
| `/api/bff/**` | `bff-smartlogix` | Enruta solicitudes hacia el Backend For Frontend. |
| `/api/auth/**` | `bff-smartlogix` | Enruta login y validacion JWT hacia el BFF. |
| `/api/inventario/**` | `ms-inventario` | Enruta solicitudes directas al microservicio de inventario. |
| `/api/pedidos/**` | `ms-pedidos` | Enruta solicitudes directas al microservicio de pedidos. |
| `/api/envios/**` | `ms-envios` | Enruta solicitudes directas al microservicio de envios. |

## 5. Relacion con Eureka

El Gateway utiliza nombres logicos registrados en Eureka:

```text
lb://bff-smartlogix
lb://ms-inventario
lb://ms-pedidos
lb://ms-envios
```

Esto permite que el Gateway no dependa de direcciones fijas. Los servicios se registran en Eureka y el Gateway los resuelve mediante `lb://`.

## 6. Configuracion relevante

Archivo:

```text
api-gateway/src/main/resources/application.properties
```

Configuracion principal:

```properties
spring.application.name=api-gateway
server.port=8085

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

spring.cloud.gateway.routes[0].id=bff
spring.cloud.gateway.routes[0].uri=lb://bff-smartlogix
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/bff/**

spring.cloud.gateway.routes[4].id=auth
spring.cloud.gateway.routes[4].uri=lb://bff-smartlogix
spring.cloud.gateway.routes[4].predicates[0]=Path=/api/auth/**
```

## 7. Monitoreo del Gateway

El Gateway expone Actuator para revisar su estado:

```text
http://localhost:8085/actuator/health
```

Respuesta esperada:

```json
{
  "status": "UP"
}
```

## 8. Ventajas

- Centraliza el punto de entrada del sistema.
- Oculta la topologia interna de microservicios.
- Facilita escalabilidad y cambios internos.
- Permite enrutar tanto API de negocio como autenticacion.
- Permite aplicar filtros transversales como CORS, logging, seguridad o monitoreo.
- Reduce el acoplamiento entre frontend y backend.

## 9. Evidencia sugerida

Para respaldar esta seccion, adjuntar capturas de:

- `api-gateway` ejecutandose en consola.
- `http://localhost:8085/actuator/health` con estado `UP`.
- Eureka mostrando `api-gateway` registrado.
- Prueba desde Gateway hacia una ruta del BFF o de microservicio.
