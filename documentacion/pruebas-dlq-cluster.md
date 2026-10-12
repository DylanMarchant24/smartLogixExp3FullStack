# Pruebas: DLQ, cluster y no regresion

Responsable: Benjamin Meneses. Rama: `feature/rabbitmq-dlq-cluster-pruebas`.

## 1. Pruebas automaticas

```bash
cd ms-notificaciones
mvn test
```

- `PedidoNotificacionListenerTest`: exito sin excepcion; fallo dentro del listener lanza `AmqpRejectAndDontRequeueException`.
- `RabbitMQDlqConfigTest`: la cola principal declara el DLX y la DLQ queda enlazada.

## 2. Prueba funcional de DLQ (evidencia: dashboard + logs)

1. Borrar la cola vieja sin DLX (una sola vez): `docker exec smartlogix-rabbitmq rabbitmqctl delete_queue smartlogix.notificaciones.queue`
2. Levantar ms-notificaciones y verificar en http://localhost:15672 que existen `smartlogix.dlx` y `smartlogix.notificaciones.dlq`.
3. En el dashboard: Exchanges > `smartlogix.exchange` > Publish message. Routing key `pedido.creado`, propiedad `content_type` = `application/json`, payload invalido: `{"pedidoId": "abc"}`.
4. El mensaje no se puede convertir a `PedidoEvent`: Spring lo rechaza sin reencolar y RabbitMQ lo envia al DLX. En los logs de ms-notificaciones aparece el warning de Spring `Execution of Rabbit message listener failed`.
5. Verificar en el dashboard que `smartlogix.notificaciones.dlq` tiene 1 mensaje (captura).
6. Opcional: activar `smartlogix.dlq.logger.enabled=true` y reiniciar para registrar el `x-death` con el prefijo `[RabbitMQ DLQ - ms-notificaciones]` (esto vacia la DLQ).

Nota: el log `[RabbitMQ DLQ - ms-notificaciones] Mensaje rechazado sin reencolar` del listener solo aparece cuando falla el procesamiento dentro del metodo (cubierto por el test unitario). Si el envio de correo falla de forma controlada, `NotificacionService` guarda la notificacion como FALLIDO y no lanza excepcion, por lo que ese caso no va a la DLQ.

## 3. Cluster (evidencia: cluster_status + dashboard)

Ver `rabbitmq/README.md`. Capturas: salida de `rabbitmqctl cluster_status` y la pestana Nodes del dashboard.

## 4. Seguridad (evidencia: Postman)

El BFF (`SecurityConfig`) deja publicos los GET de `/api/bff/**` y protege con JWT de Azure los POST/PUT/PATCH/DELETE de inventario, pedidos y envios.

Importar `postman/RabbitMQ-DLQ-Seguridad.postman_collection.json` y configurar `jwt` (token Azure vigente) y `sku` (SKU existente en inventario). Esperado: GET `/api/bff/pedidos` responde 200 sin token; POST `/api/bff/pedidos` responde 401 sin token y con token invalido, y 201 con token valido.

## 5. No regresion

Con todo levantado: crear un pedido desde el frontend (o el caso 201 de Postman) y confirmar que (a) el pedido se guarda, (b) ms-notificaciones registra la notificacion y (c) la DLQ queda en 0 mensajes.

## Checklist de evidencias para AVA

- [ ] Resultado de `mvn test`
- [ ] Dashboard con DLX, DLQ y binding
- [ ] Mensaje invalido publicado y DLQ con 1 mensaje
- [ ] Logs de ms-notificaciones tras el rechazo
- [ ] `cluster_status` con 2 nodos
- [ ] Resultado Postman de seguridad
- [ ] Flujo normal sin regresion
