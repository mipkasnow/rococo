#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

docker compose -f docker/kafka/docker-compose.yml up -d --wait

echo 'Rococo Kafka: localhost:9092'
echo 'Rococo Kafka UI: http://localhost:8085'
