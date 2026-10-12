# Pruebas: DLQ, cluster y no regresion

Responsable: Benjamin Meneses. Rama: `feature/rabbitmq-dlq-cluster-pruebas`.

## 1. Pruebas automaticas

```bash
cd ms-notificaciones
mvn test
```

- `PedidoNotificacionListenerTest`: exito sin excepcion; fallo lanza `AmqpRejectAndDontRequeueException`.
- `RabbitMQDlqConfigTest`: la cola principal declara el DLX y la DLQ queda enlazada.

## 2. Prueba funcional de DLQ (evidencia: dashboard + logs)

1. Borrar la cola vieja sin DLX (una sola vez): `docker exec smartlogix-rabbitmq rabbitmqctl delete_queue smartlogix.notificaciones.queue`
2. Levantar ms-notificaciones y verificar en http://localhost:15672 que existen `smartlogix.dlx` y `smartlogix.notificaciones.dlq`.
3. Provocar un fallo (por ejemplo detener MySQL) y crear un pedido.
4. Verificar en logs el mensaje `[RabbitMQ DLQ - ms-notificaciones]`.
5. Verificar en el dashboard que la DLQ tiene 1 mensaje (captura).
6. Opcional: activar `smartlogix.dlq.logger.enabled=true` para registrar el `x-death` en logs (esto vacia la DLQ).

## 3. Cluster (evidencia: cluster_status + dashboard)

Ver `rabbitmq/README.md`. Capturas: salida de `rabbitmqctl cluster_status` y la pestana Nodes del dashboard.

## 4. Seguridad (evidencia: Postman)

Importar `postman/RabbitMQ-DLQ-Seguridad.postman_collection.json`, ajustar `pedidos_path` y `jwt`. Esperado: sin token y token invalido responden 401; token valido responde 200.

## 5. No regresion

Con todo levantado y MySQL funcionando: crear un pedido desde el frontend y confirmar que (a) el pedido se guarda, (b) ms-notificaciones registra la notificacion y (c) la DLQ queda en 0 mensajes.

## Checklist de evidencias para AVA

- [ ] Resultado de `mvn test`
- [ ] Dashboard con DLX, DLQ y binding
- [ ] Logs con `[RabbitMQ DLQ - ms-notificaciones]`
- [ ] DLQ con mensaje tras el fallo
- [ ] `cluster_status` con 2 nodos
- [ ] Resultado Postman de seguridad
- [ ] Flujo normal sin regresion
