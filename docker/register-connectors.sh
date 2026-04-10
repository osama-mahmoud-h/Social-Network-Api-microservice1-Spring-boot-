#!/bin/bash
set -euo pipefail

CONNECT_URL="${KAFKA_CONNECT_URL:-http://localhost:8778}"
REPO_ROOT="$(git -C "$(dirname "$0")" rev-parse --show-toplevel)"

load_env() {
  local file="$1"
  [[ -f "$file" ]] || return 0
  while IFS= read -r line || [[ -n "$line" ]]; do
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    [[ -z "${line// }" ]] && continue
    local key="${line%%=*}"
    local value="${line#*=}"
    export "$key"="$value"
  done < "$file"
}

load_env "$REPO_ROOT/.env"

shopt -s nullglob

echo "=== Creating Postgres publications ==="
for pub_sql in "$REPO_ROOT"/services/*/debezium/*.pub.sql; do
  load_env "$(dirname "$(dirname "$pub_sql")")/.env"
  db_host="${DATABASE_HOST:-localhost}"
  db_port="${DATABASE_PORT:-5432}"
  db_user="${DATABASE_USERNAME:-osama}"
  db_name="${DATABASE_NAME:-$(basename "$(dirname "$(dirname "$pub_sql")")" | sed 's/-service//')_db}"

  local_host="${DATABASE_LOCAL_HOST:-localhost}"
  echo "→ $(basename "$pub_sql") on $local_host:$db_port/$db_name"
  PGPASSWORD="${DATABASE_PASSWORD:-}" psql -h "$local_host" -p "$db_port" -U "$db_user" -d "$db_name" -f "$pub_sql" \
    || { echo "FAILED: $(basename "$pub_sql")"; exit 1; }
done

echo "=== Waiting for Kafka Connect ==="
for i in $(seq 1 24); do
  curl -sf "$CONNECT_URL/connectors" > /dev/null && break
  echo "  ${i}x5s ..."; sleep 5
done
curl -sf "$CONNECT_URL/connectors" > /dev/null || { echo "Kafka Connect not ready"; exit 1; }

echo "=== Registering connectors ==="
CONNECTOR_FILES=("$REPO_ROOT"/services/*/debezium/*.json)
[[ ${#CONNECTOR_FILES[@]} -eq 0 ]] && echo "No connector files found" && exit 0

for cfg in "${CONNECTOR_FILES[@]}"; do
  load_env "$(dirname "$(dirname "$cfg")")/.env"
  RESOLVED=$(envsubst < "$cfg")
  NAME=$(echo "$RESOLVED" | python3 -c "import sys,json; print(json.load(sys.stdin)['name'])")
  echo "→ $NAME"
  if curl -sf "$CONNECT_URL/connectors/$NAME" > /dev/null 2>&1; then
    curl -sf -X DELETE "$CONNECT_URL/connectors/$NAME"; sleep 2
  fi
  echo "$RESOLVED" | curl -sf -X POST "$CONNECT_URL/connectors" \
    -H "Content-Type: application/json" --data-binary @- \
    || { echo "FAILED: $NAME"; exit 1; }
done

echo "=== Status ==="
curl -sf "$CONNECT_URL/connectors?expand=status" | python3 -m json.tool 2>/dev/null \
  || curl -sf "$CONNECT_URL/connectors"