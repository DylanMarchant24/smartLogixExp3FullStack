# Monitoreo de Microservicios - SmartLogix EP3

## 1. Objetivo

SmartLogix utiliza Spring Boot Actuator para exponer endpoints de monitoreo en los servicios backend. Esto permite verificar el estado de cada componente de la arquitectura antes y durante las pruebas del sistema.

Esta documentacion se relaciona principalmente con la rama:

```text
feature/ricardo-discovery-gateway
```

## 2. Problema que resuelve

En una solucion con microservicios, es importante saber si cada componente esta funcionando correctamente. Actuator permite revisar rapidamente la salud de cada servicio sin depender solo de la interfaz grafica o de los logs.

## 3. Endpoints principales

| Servicio | Endpoint de salud |
|---|---|
| Discovery Server | `http://localhost:8761/actuator/health` |
| API Gateway | `http://localhost:8085/actuator/health` |
| BFF | `http://localhost:8080/actuator/health` |
| Inventario | `http://localhost:8081/actuator/health` |
| Pedidos | `http://localhost:8082/actuator/health` |
| Envios | `http://localhost:8083/actuator/health` |

## 4. Configuracion utilizada

En los servicios se expone Actuator con configuraciones similares a:

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

En el API Gateway tambien se expone informacion de gateway:

```properties
management.endpoints.web.exposure.include=health,info,metrics,gateway
```

## 5. Uso esperado

Los endpoints de monitoreo permiten:

- Verificar si un servicio esta activo.
- Detectar fallas en componentes especificos.
- Confirmar que el sistema esta listo antes de probar el frontend.
- Apoyar la defensa tecnica mostrando evidencia de disponibilidad.
- Revisar problemas de integracion entre Gateway, BFF y microservicios.

## 6. Ejemplo de respuesta esperada

```json
{
  "status": "UP"
}
```

Cuando el estado aparece como `UP`, significa que el componente esta activo y disponible.

## 7. Acciones si falla un microservicio

1. Revisar `/actuator/health` del servicio afectado.
2. Revisar si el servicio aparece registrado en Eureka.
3. Revisar logs de la terminal.
4. Validar que MySQL este activo, si el servicio depende de base de datos.
5. Reiniciar el servicio afectado.
6. Probar nuevamente desde el API Gateway.

## 8. Evidencia sugerida

Adjuntar capturas de:

- Cada endpoint `/actuator/health` con estado `UP`.
- Eureka mostrando los servicios registrados.
- API Gateway respondiendo correctamente.
