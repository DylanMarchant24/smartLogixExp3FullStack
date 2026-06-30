# Service Discovery - SmartLogix EP3

## 1. Objetivo

SmartLogix incorpora un servidor Eureka como Service Discovery para registrar dinamicamente los servicios disponibles en la arquitectura.

Esta funcionalidad corresponde principalmente a la rama:

```text
feature/ricardo-discovery-gateway
```

## 2. Problema que resuelve

En una arquitectura de microservicios, las direcciones de red pueden cambiar. Service Discovery evita configurar manualmente cada URL y permite que los servicios se encuentren mediante nombres logicos.

Gracias a Eureka, el API Gateway puede enrutar a los servicios registrados usando `lb://` en vez de depender de una IP o puerto fijo.

## 3. Componente implementado

El servidor Eureka se encuentra en:

```text
discovery-server/
```

Archivo de configuracion principal:

```text
discovery-server/src/main/resources/application.properties
```

Configuracion base:

```properties
spring.application.name=discovery-server
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

## 4. Puerto

Eureka se ejecuta en:

```text
http://localhost:8761
```

## 5. Componentes esperados en Eureka

Los servicios que deben registrarse son:

- `api-gateway`
- `bff-smartlogix`
- `ms-inventario`
- `ms-pedidos`
- `ms-envios`

## 6. Configuracion en clientes Eureka

Cada servicio cliente usa una configuracion similar a:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

## 7. Relacion con API Gateway

El Gateway usa los nombres registrados en Eureka para enrutar solicitudes:

```text
lb://bff-smartlogix
lb://ms-inventario
lb://ms-pedidos
lb://ms-envios
```

De esta forma, el Gateway no necesita conocer directamente las URLs internas de cada servicio.

## 8. Ventajas

- Registro dinamico de servicios.
- Menor acoplamiento entre componentes.
- Mejor escalabilidad.
- Facilita balanceo con `lb://`.
- Permite verificar rapidamente que los servicios esten activos.

## 9. Desventajas o consideraciones

- Agrega un componente adicional a mantener.
- Si Eureka no esta activo, nuevos servicios no podran registrarse.
- El orden de ejecucion importa: primero Eureka, luego Gateway, BFF y microservicios.

## 10. Evidencia sugerida

Adjuntar captura de:

- Consola de `discovery-server` ejecutandose.
- Navegador en `http://localhost:8761`.
- Servicios registrados en Eureka.
