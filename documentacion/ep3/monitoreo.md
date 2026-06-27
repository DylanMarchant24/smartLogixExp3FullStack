# Monitoreo de Microservicios - SmartLogix

SmartLogix utiliza Spring Boot Actuator para exponer endpoints de monitoreo en los servicios backend. Esto permite verificar el estado de cada componente de la arquitectura.

## Problema que resuelve

En una solución con microservicios, es importante saber si cada componente está funcionando correctamente. Actuator permite revisar rápidamente la salud de cada servicio sin tener que analizar todo el código o depender solo de la interfaz gráfica.

## Endpoints principales

| Servicio | Endpoint de salud |
|---|---|
| API Gateway | http://localhost:8085/actuator/health |
| BFF | http://localhost:8080/actuator/health |
| Inventario | http://localhost:8081/actuator/health |
| Pedidos | http://localhost:8082/actuator/health |
| Envios | http://localhost:8083/actuator/health |
| Discovery Server | http://localhost:8761/actuator/health |

## Uso esperado

Los endpoints de monitoreo permiten:

- Verificar si un servicio está activo.
- Detectar fallas en componentes específicos.
- Comprobar el estado general antes de probar el frontend.
- Apoyar la defensa técnica mostrando evidencia de disponibilidad.

## Acciones si falla un microservicio

1. Revisar el endpoint /actuator/health.
2. Revisar si el servicio aparece registrado en Eureka.
3. Revisar logs de la terminal.
4. Validar conexión a MySQL.
5. Reiniciar el servicio afectado.
6. Probar nuevamente desde el API Gateway.

## Ejemplo de respuesta esperada

Respuesta esperada:

{
  "status": "UP"
}

Cuando el estado aparece como UP, significa que el componente está activo y disponible.
