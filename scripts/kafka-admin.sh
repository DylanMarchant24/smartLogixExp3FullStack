#!/bin/bash
# ==============================================================================
# SmartLogix - Script de Administracion de Apache Kafka y Consumer Groups
# Uso:
#   ./scripts/kafka-admin.sh list-topics
#   ./scripts/kafka-admin.sh describe-topics
#   ./scripts/kafka-admin.sh create-topic <nombre> [particiones] [replicas] [retention_ms]
#   ./scripts/kafka-admin.sh update-retention <nombre> <retention_ms>
#   ./scripts/kafka-admin.sh delete-topic <nombre>
#   ./scripts/kafka-admin.sh list-groups
#   ./scripts/kafka-admin.sh describe-group <group_id>
# ==============================================================================

set -e

CONTAINER="${KAFKA_CONTAINER:-smartlogix-kafka-1}"
BOOTSTRAP="${KAFKA_BOOTSTRAP:-kafka-1:29092,kafka-2:29093}"
ACTION="${1:-help}"

case "$ACTION" in
  list-topics)
    docker exec "$CONTAINER" kafka-topics --bootstrap-server "$BOOTSTRAP" --list
    ;;
  describe-topics)
    docker exec "$CONTAINER" kafka-topics --bootstrap-server "$BOOTSTRAP" --describe
    ;;
  create-topic)
    TOPIC="${2:?Debe indicar el nombre del topico}"
    PARTITIONS="${3:-3}"
    REPLICAS="${4:-2}"
    RETENTION="${5:-604800000}"
    docker exec "$CONTAINER" kafka-topics --bootstrap-server "$BOOTSTRAP" \
      --create --if-not-exists \
      --topic "$TOPIC" \
      --partitions "$PARTITIONS" \
      --replication-factor "$REPLICAS" \
      --config retention.ms="$RETENTION" \
      --config cleanup.policy=delete \
      --config min.insync.replicas=1
    ;;
  update-retention)
    TOPIC="${2:?Debe indicar el nombre del topico}"
    RETENTION="${3:?Debe indicar retention.ms (ej. 86400000)}"
    docker exec "$CONTAINER" kafka-configs --bootstrap-server "$BOOTSTRAP" \
      --entity-type topics --entity-name "$TOPIC" \
      --alter --add-config retention.ms="$RETENTION"
    ;;
  delete-topic)
    TOPIC="${2:?Debe indicar el nombre del topico a eliminar}"
    docker exec "$CONTAINER" kafka-topics --bootstrap-server "$BOOTSTRAP" \
      --delete --topic "$TOPIC"
    ;;
  list-groups)
    docker exec "$CONTAINER" kafka-consumer-groups --bootstrap-server "$BOOTSTRAP" --list
    ;;
  describe-group)
    GROUP="${2:-smartlogix-auditoria-group}"
    docker exec "$CONTAINER" kafka-consumer-groups --bootstrap-server "$BOOTSTRAP" \
      --describe --group "$GROUP"
    ;;
  *)
    echo "Uso: $0 {list-topics|describe-topics|create-topic|update-retention|delete-topic|list-groups|describe-group}"
    exit 1
    ;;
esac

