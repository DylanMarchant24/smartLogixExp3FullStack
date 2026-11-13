#!/bin/bash
set -e

BOOTSTRAP_SERVERS="${KAFKA_BOOTSTRAP_SERVERS:-kafka-1:29092,kafka-2:29093}"
TOPIC_PEDIDOS="${KAFKA_TOPIC_PEDIDOS:-smartlogix.pedidos.eventos}"
TOPIC_ENVIOS="${KAFKA_TOPIC_ENVIOS:-smartlogix.envios.tracking}"

echo "=================================================================="
echo " SmartLogix - Inicializando Topicos de Apache Kafka"
echo " Bootstrap Servers: ${BOOTSTRAP_SERVERS}"
echo "=================================================================="

echo "[1/3] Creando topico de eventos de pedidos: ${TOPIC_PEDIDOS}"
kafka-topics --bootstrap-server "${BOOTSTRAP_SERVERS}" \
  --create --if-not-exists \
  --topic "${TOPIC_PEDIDOS}" \
  --partitions 3 \
  --replication-factor 2 \
  --config retention.ms=604800000 \
  --config cleanup.policy=delete \
  --config min.insync.replicas=1 \
  --config segment.bytes=1073741824

echo "[2/3] Creando topico de tracking de envios: ${TOPIC_ENVIOS}"
kafka-topics --bootstrap-server "${BOOTSTRAP_SERVERS}" \
  --create --if-not-exists \
  --topic "${TOPIC_ENVIOS}" \
  --partitions 3 \
  --replication-factor 2 \
  --config retention.ms=604800000 \
  --config cleanup.policy=delete \
  --config min.insync.replicas=1 \
  --config segment.bytes=1073741824

echo "[3/3] Verificando topicos creados y sus parametros:"
kafka-topics --bootstrap-server "${BOOTSTRAP_SERVERS}" --describe --topic "${TOPIC_PEDIDOS}"
kafka-topics --bootstrap-server "${BOOTSTRAP_SERVERS}" --describe --topic "${TOPIC_ENVIOS}"

echo "=================================================================="
echo " SmartLogix - Topicos de Kafka inicializados correctamente"
echo "=================================================================="

