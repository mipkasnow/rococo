#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

docker compose -f docker/minio/docker-compose.yml up -d --wait minio
docker compose -f docker/minio/docker-compose.yml run --rm -T --no-deps createbuckets

echo 'Rococo MinIO API: http://localhost:9100'
echo 'Rococo MinIO Console: http://localhost:9101'
echo 'User: user, password: password'
echo 'Buckets: painting, museum, artist, avatar'
