#!/bin/bash
set -euo pipefail

# Только проверка занятости портов Rococo. Ничего не убивает и не останавливает.

ports=(9000 8080 3000 5433 9092 9100 9101 8085)
busy=0

for port in "${ports[@]}"; do
  pids="$(lsof -ti tcp:"${port}" 2>/dev/null || true)"
  if [ -n "${pids}" ]; then
    busy=1
    echo "Port ${port} busy: ${pids}"
  else
    echo "Port ${port} is free"
  fi
done

if [ "${busy}" -eq 1 ]; then
  echo "Освободите порты вручную. Этот скрипт не завершает чужие процессы." >&2
  exit 1
fi
