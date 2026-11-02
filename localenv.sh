#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

CONTAINER_NAME='rococo-postgres'
VOLUME_NAME='rococo-pg'
HOST_PORT='5433'
IMAGE='postgres:17'
INIT_DIR="$PWD/docker/postgres/init"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker не найден. Установите Docker Desktop и повторите." >&2
  exit 1
fi

if ! docker info >/dev/null 2>&1; then
  echo "Docker daemon недоступен. Запустите Docker и повторите." >&2
  exit 1
fi

port_owner_cid() {
  docker ps -a --filter "publish=${HOST_PORT}" --format '{{.ID}} {{.Names}}' | awk 'NF{print; exit}'
}

ensure_port_free_or_ours() {
  local owner
  owner="$(port_owner_cid || true)"
  if [ -z "${owner}" ]; then
    return 0
  fi
  local owner_name
  owner_name="$(echo "${owner}" | awk '{print $2}')"
  if [ "${owner_name}" = "${CONTAINER_NAME}" ]; then
    return 0
  fi
  echo "Порт ${HOST_PORT} занят контейнером '${owner_name}'. Скрипт не трогает чужие контейнеры." >&2
  exit 1
}

start_existing() {
  local status
  status="$(docker inspect -f '{{.State.Status}}' "${CONTAINER_NAME}")"
  if [ "${status}" = "running" ]; then
    echo "Контейнер ${CONTAINER_NAME} уже запущен."
    return 0
  fi
  docker start "${CONTAINER_NAME}" >/dev/null
  echo "Контейнер ${CONTAINER_NAME} запущен."
}

create_new() {
  ensure_port_free_or_ours
  docker run --name "${CONTAINER_NAME}" \
    -p "${HOST_PORT}:5432" \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=secret \
    -e POSTGRES_DB=postgres \
    -v "${VOLUME_NAME}:/var/lib/postgresql/data" \
    -v "${INIT_DIR}:/docker-entrypoint-initdb.d:ro" \
    -d "${IMAGE}" >/dev/null
  echo "Создан и запущен контейнер ${CONTAINER_NAME}."
}

if docker inspect "${CONTAINER_NAME}" >/dev/null 2>&1; then
  image="$(docker inspect -f '{{.Config.Image}}' "${CONTAINER_NAME}")"
  if [ "${image}" != "${IMAGE}" ]; then
    echo "Контейнер ${CONTAINER_NAME} существует, но образ '${image}' ≠ '${IMAGE}'." >&2
    echo "Скрипт не удаляет и не пересоздаёт чужие/неожиданные контейнеры." >&2
    exit 1
  fi
  ensure_port_free_or_ours
  start_existing
else
  create_new
fi

echo "Rococo Postgres: localhost:${HOST_PORT}"
echo "User: postgres, password: secret"
echo "Databases: rococo_gateway, rococo_auth"
echo "Скрипт управляет только контейнером '${CONTAINER_NAME}' и volume '${VOLUME_NAME}'."
