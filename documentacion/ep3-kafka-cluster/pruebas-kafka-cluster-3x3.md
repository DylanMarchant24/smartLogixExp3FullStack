# Cluster Kafka 3 Zookeeper + 3 brokers: configuracion y pruebas (Benjamin Meneses)

Archivos: `docker-compose.kafka-cluster.yml`, `docker-compose.kafka-ui.yml`, `scripts/kafka-cluster-3x3.sh`.
Son independientes del `docker-compose.yml` principal (proyecto, red `smartlogix-kafka-net`, volumenes, contenedores `sl3-*` y puertos propios), por lo que no interfieren con el trabajo del resto del equipo.

| Elemento | Valor |
|---|---|
| Zookeeper | 3 nodos (quorum 2 de 3) |
| Brokers | 3 (puertos externos 19092, 19093, 19094; internos 29092, 29093, 29094) |
| Topicos | `smartlogix.pedidos.eventos`, `smartlogix.envios.tracking` |
| Particiones / replicas | 3 / 3 |
| min.insync.replicas | 2 (con `acks=all` tolera la caida de 1 broker sin perder datos) |
| Retencion / cleanup | 7 dias (604800000 ms) / delete |
| Auto-creacion de topicos | desactivada |
| Kafka UI | http://localhost:8096 |

## Levantar
```bash
export HOST_IP=localhost   # en AWS: export HOST_IP=$(curl -s ifconfig.me)
docker compose -f docker-compose.kafka-cluster.yml up -d
docker compose -f docker-compose.kafka-ui.yml up -d
docker logs sl3-kafka-init | tail -40
```

## Pruebas
| # | Comando | Resultado esperado |
|---|---|---|
| 1 | `bash scripts/kafka-cluster-3x3.sh quorum` | 1 leader y 2 follower |
| 2 | `bash scripts/kafka-cluster-3x3.sh topics` | PartitionCount 3, ReplicationFactor 3, Isr con 3 brokers, min.insync.replicas=2, retention.ms=604800000, cleanup.policy=delete |
| 3 | `docker exec sl3-kafka-1 kafka-console-producer --bootstrap-server kafka-1:29092 --topic no.existe <<< x` | Error: el topico no se crea (auto-creacion desactivada) |
| 4 | `bash scripts/kafka-cluster-3x3.sh produce smartlogix.pedidos.eventos` y luego `groups` | Mensaje con particion y offset; en un consumer group se ve CURRENT-OFFSET, LOG-END-OFFSET y LAG |
| 5 | `kill-broker 2`, luego `VIA=1 ... topics` y `produce smartlogix.pedidos.eventos all`, luego `start-broker 2` | Isr baja a 2, se reasigna el lider, la publicacion funciona y el Isr vuelve a 3 |
| 6 | `kill-broker 2` y `kill-broker 3`, luego `produce ... all` | Error NOT_ENOUGH_REPLICAS (prioriza durabilidad); luego `start-broker 2` y `start-broker 3` |
| 7 | `kill-zk 3`, luego `quorum` y `topics` | 1 leader y 1 follower; Kafka sigue operando; luego `start-zk 3` |

Consumer group para ver lag y reparto de particiones:
```bash
docker exec -it sl3-kafka-1 kafka-console-consumer --bootstrap-server kafka-1:29092 --topic smartlogix.pedidos.eventos --group demo-grupo
```
(abrir en dos terminales con el mismo `--group`; detener ambos, producir 5 mensajes y observar el LAG en `groups` y en Kafka UI).

## Demo en Kafka UI (http://localhost:8096)
1. Brokers: 3 activos, particiones por broker, controlador.
2. Topics: 3 particiones, RF 3, ISR y configuracion.
3. Messages: particion, offset y payload.
4. Consumers: offsets, lag y asignacion de particiones.
5. Detener un broker (`kill-broker 2`) y mostrar under-replicated partitions y la recuperacion.
6. Crear, editar la retencion y borrar un topico de prueba desde la UI.

## Justificacion
- 3 Zookeeper: quorum por mayoria; con 3 nodos se tolera la caida de 1 (con 2 nodos no habria tolerancia).
- 3 brokers y RF 3: una copia de cada particion en cada broker; si cae el lider se elige otro entre el ISR.
- min.insync.replicas=2 con acks=all: una escritura se confirma con al menos 2 copias; con 1 broker caido sigue funcionando y con 2 caidos se rechaza la escritura.
- 3 particiones: hasta 3 consumidores en paralelo dentro de un grupo.

## Integracion con los microservicios (pendiente, fuera de este commit)
Este cluster corre en su propia red. Para que `ms-pedidos`, `ms-envios` y `ms-auditoria` lo usen hay que cambiar `SPRING_KAFKA_BOOTSTRAP_SERVERS` en el compose principal (archivo de otros integrantes) y unirlos a la red `smartlogix-kafka-net`, con acuerdo del equipo. Verificar ademas `acks=all` e idempotencia en los productores.
