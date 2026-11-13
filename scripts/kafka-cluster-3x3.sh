#!/bin/bash
# SmartLogix EP3 - Verificacion del cluster Kafka 3x3 (Benjamin Meneses). No modifica scripts/kafka-admin.sh.
# Uso: bash scripts/kafka-cluster-3x3.sh {quorum|topics|groups|produce <topico> [acks]|kill-broker N|start-broker N|kill-zk N|start-zk N}
# Si el broker 1 esta caido, ejecutar via otro: VIA=3 bash scripts/kafka-cluster-3x3.sh topics
set -e
BOOT="kafka-1:29092,kafka-2:29093,kafka-3:29094"
EXEC="docker exec sl3-kafka-${VIA:-1}"

case "${1:-help}" in
  quorum)
    for i in 1 2 3; do
      echo -n "zookeeper-$i: "
      docker exec sl3-zookeeper-$i bash -c "echo stat | nc localhost 2181 | grep Mode" 2>/dev/null || echo "caido"
    done ;;
  topics)
    $EXEC kafka-topics --bootstrap-server "$BOOT" --describe ;;
  groups)
    $EXEC kafka-consumer-groups --bootstrap-server "$BOOT" --describe --all-groups ;;
  produce)
    echo "{\"test\":\"$(date +%s)\"}" | $EXEC kafka-console-producer --bootstrap-server "$BOOT" \
      --topic "${2:?Indique el topico}" --producer-property acks="${3:-all}" ;;
  kill-broker)  docker stop  "sl3-kafka-${2:?Indique el broker 1-3}" ;;
  start-broker) docker start "sl3-kafka-${2:?Indique el broker 1-3}" ;;
  kill-zk)      docker stop  "sl3-zookeeper-${2:?Indique el nodo 1-3}" ;;
  start-zk)     docker start "sl3-zookeeper-${2:?Indique el nodo 1-3}" ;;
  *)
    echo "Uso: $0 {quorum|topics|groups|produce <topico> [acks]|kill-broker N|start-broker N|kill-zk N|start-zk N}"
    exit 1 ;;
esac
