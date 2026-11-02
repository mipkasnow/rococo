#!/bin/bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

RUN_DIR="$ROOT/.run"
mkdir -p "$RUN_DIR"

AUTH_PORT=9000
GATEWAY_PORT=8080
FRONT_PORT=3000
# /login без OAuth-сессии отдаёт 302 на фронт; /register стабильно 200
AUTH_URL="http://localhost:${AUTH_PORT}/register"
GATEWAY_URL="http://localhost:${GATEWAY_PORT}/api/country?page=0&size=1"
FRONT_URL="http://localhost:${FRONT_PORT}"
FRONT_OPEN_URL="http://localhost:${FRONT_PORT}"

usage() {
  cat <<'EOF'
Usage:
  ./run-services.sh          поднять infra + auth + gateway + client и открыть браузер
  ./run-services.sh stop     остановить только процессы, которые скрипт сам запускал
EOF
}

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Нужна команда '$1'." >&2
    exit 1
  fi
}

port_pids() {
  lsof -ti tcp:"$1" 2>/dev/null || true
}

port_busy() {
  [ -n "$(port_pids "$1")" ]
}

http_code() {
  curl -s -o /dev/null -w '%{http_code}' --max-time 2 "$1" || true
}

auth_ready() {
  [ "$(http_code "$AUTH_URL")" = "200" ]
}

gateway_ready() {
  local code
  code="$(http_code "$GATEWAY_URL")"
  [ "$code" = "200" ]
}

front_ready() {
  local code
  code="$(http_code "$FRONT_URL")"
  [ "$code" = "200" ] || [ "$code" = "304" ]
}

wait_until() {
  local name=$1
  local timeout=$2
  shift 2
  local started now elapsed
  started="$(date +%s)"
  echo "Жду готовности: ${name} (таймаут ${timeout}s, лог: ${RUN_DIR}/${name}.log)"
  while true; do
    if "$@"; then
      echo "${name}: ready"
      return 0
    fi
    now="$(date +%s)"
    elapsed=$((now - started))
    if (( elapsed >= timeout )); then
      echo "${name}: таймаут ${timeout}s. Смотри ${RUN_DIR}/${name}.log" >&2
      return 1
    fi
    if (( elapsed > 0 && elapsed % 20 == 0 )); then
      echo "  … ${name} ещё не ready (${elapsed}s)"
    fi
    sleep 2
  done
}

postgres_ready() {
  docker exec rococo-postgres pg_isready -U postgres >/dev/null 2>&1
}

kill_tree() {
  local pid=$1
  local child
  for child in $(pgrep -P "$pid" 2>/dev/null || true); do
    kill_tree "$child"
  done
  kill "$pid" 2>/dev/null || true
}

stop_owned() {
  local name=$1
  local pid_file="$RUN_DIR/${name}.pid"
  local started_file="$RUN_DIR/${name}.started"
  if [ ! -f "$started_file" ]; then
    rm -f "$pid_file"
    return 0
  fi
  if [ -f "$pid_file" ]; then
    local pid
    pid="$(cat "$pid_file")"
    if kill -0 "$pid" 2>/dev/null; then
      echo "Останавливаю ${name} (pid ${pid})"
      kill_tree "$pid"
      wait "$pid" 2>/dev/null || true
    fi
  fi
  rm -f "$pid_file" "$started_file"
}

stop_all_apps() {
  stop_owned client
  stop_owned gateway
  stop_owned auth
  echo "Приложения остановлены. Docker-контейнеры Rococo не трогались."
}

ensure_free_or_ready() {
  local port=$1
  local name=$2
  shift 2
  if "$@"; then
    echo "${name}: уже отвечает на порту ${port}, переиспользую"
    return 0
  fi
  if port_busy "$port"; then
    echo "Порт ${port} занят, но ${name} не готов. Чужой процесс не трогаю." >&2
    echo "PIDs: $(port_pids "$port")" >&2
    exit 1
  fi
  return 1
}

start_gradle() {
  local name=$1
  local module=$2
  local port=$3
  shift 3
  if ensure_free_or_ready "$port" "$name" "$@"; then
    return 0
  fi
  echo "Стартую ${module}"
  (
    cd "$ROOT"
    ./gradlew ":${module}:bootRun"
  ) >"$RUN_DIR/${name}.log" 2>&1 &
  echo $! >"$RUN_DIR/${name}.pid"
  touch "$RUN_DIR/${name}.started"
}

start_client() {
  if ensure_free_or_ready "$FRONT_PORT" "client" front_ready; then
    return 0
  fi
  if [ ! -d "$ROOT/rococo-client/node_modules" ]; then
    echo "npm install"
    (cd "$ROOT/rococo-client" && npm install)
  fi
  echo "Стартую rococo-client"
  (
    cd "$ROOT/rococo-client"
    npm run dev
  ) >"$RUN_DIR/client.log" 2>&1 &
  echo $! >"$RUN_DIR/client.pid"
  touch "$RUN_DIR/client.started"
}

open_browser() {
  if command -v open >/dev/null 2>&1; then
    open "$FRONT_OPEN_URL"
  elif command -v xdg-open >/dev/null 2>&1; then
    xdg-open "$FRONT_OPEN_URL" >/dev/null 2>&1 || true
  else
    echo "Откройте вручную: ${FRONT_OPEN_URL}"
  fi
}

start_all() {
  require_cmd docker
  require_cmd java
  require_cmd curl
  require_cmd npm
  require_cmd lsof

  if ! docker info >/dev/null 2>&1; then
    echo "Docker daemon недоступен." >&2
    exit 1
  fi

  echo "== Infra =="
  "$ROOT/localenv.sh"
  wait_until "postgres" 60 postgres_ready
  "$ROOT/kafka.sh"
  "$ROOT/minio.sh"

  echo "== Backend =="
  start_gradle auth rococo-auth "$AUTH_PORT" auth_ready
  wait_until "auth" 300 auth_ready
  start_gradle gateway rococo-gateway "$GATEWAY_PORT" gateway_ready
  wait_until "gateway" 300 gateway_ready

  echo "== Frontend =="
  start_client
  wait_until "client" 120 front_ready
  open_browser

  echo
  echo "Rococo up:"
  echo "  UI:         ${FRONT_OPEN_URL}"
  echo "  Auth:       http://localhost:${AUTH_PORT}"
  echo "  Gateway:    http://localhost:${GATEWAY_PORT}"
  echo "  Swagger:    http://localhost:${GATEWAY_PORT}/swagger-ui/index.html"
  echo "  Logs:       ${RUN_DIR}/"
  echo "Ctrl+C или ./run-services.sh stop — остановит auth/gateway/client."
  echo "Контейнеры postgres/kafka/minio останутся запущенными."

  trap 'echo; stop_all_apps; exit 0' INT TERM
  while true; do
    sleep 3600
  done
}

cmd="${1:-start}"
case "$cmd" in
  start|"")
    start_all
    ;;
  stop)
    stop_all_apps
    ;;
  -h|--help|help)
    usage
    ;;
  *)
    usage >&2
    exit 1
    ;;
esac
