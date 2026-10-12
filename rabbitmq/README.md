# Cluster RabbitMQ (2 nodos)

Basado en la guia 2.3.1 "RabbitMQ Cluster con Docker Compose". No modifica `docker-compose.yml`.

## Levantar

```bash
# 1. Detener el rabbitmq del compose principal (usa los mismos puertos 5672/15672)
docker stop smartlogix-rabbitmq

# 2. Levantar el cluster
docker compose -f docker-compose.rabbit-cluster.yml up -d
docker compose -f docker-compose.rabbit-cluster.yml ps
```

## Verificar

```bash
docker exec rabbitmq-1 rabbitmqctl cluster_status
```

Debe listar `rabbit@rabbitmq-1` y `rabbit@rabbitmq-2` en running nodes.

- Nodo 1: http://localhost:15672
- Nodo 2: http://localhost:15673
- Credenciales por defecto: smartlogix / smartlogix123 (sobrescribibles con `.env`)

## Replicar colas entre nodos

```bash
docker exec rabbitmq-1 rabbitmqctl set_policy ha-smartlogix "^smartlogix\." '{"ha-mode":"all","ha-sync-mode":"automatic"}' --apply-to queues
```

## Prueba de tolerancia a fallos

```bash
docker stop rabbitmq-1
docker exec rabbitmq-2 rabbitmqctl cluster_status
docker start rabbitmq-1
```

## Conectar los microservicios (opcional)

Los microservicios usan `SPRING_RABBITMQ_HOST=rabbitmq`. Para probar con el cluster, ejecutarlos con
`SPRING_RABBITMQ_ADDRESSES=localhost:5672,localhost:5673` (o apuntar al nodo 1). No se modifica la configuracion existente.
