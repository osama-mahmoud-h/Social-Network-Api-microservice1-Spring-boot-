#!/bin/bash
# Registers the Debezium outbox connector with Kafka Connect.
# Run this after kafka-connect is healthy:
#   docker-compose -f docker-compose-all.yml up -d kafka-connect
#   ./register-connectors.sh

CONNECT_URL="${KAFKA_CONNECT_URL:-http://localhost:8083}"
MAX_WAIT=120
WAITED=0

echo "Waiting for Kafka Connect at $CONNECT_URL ..."
until curl -sf "$CONNECT_URL/connectors" > /dev/null; do
  if [ $WAITED -ge $MAX_WAIT ]; then
    echo "ERROR: Kafka Connect did not become ready within ${MAX_WAIT}s"
    exit 1
  fi
  sleep 5
  WAITED=$((WAITED + 5))
  echo "  ... still waiting (${WAITED}s)"
done
echo "Kafka Connect is ready."

# Delete existing connector if present (idempotent re-registration)
EXISTING=$(curl -sf "$CONNECT_URL/connectors/outbox-connector" | grep -c "outbox-connector" || true)
if [ "$EXISTING" -gt 0 ]; then
  echo "Removing existing outbox-connector ..."
  curl -sf -X DELETE "$CONNECT_URL/connectors/outbox-connector"
  sleep 2
fi

echo "Registering outbox-connector ..."
curl -sf -X POST "$CONNECT_URL/connectors" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "outbox-connector",
    "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "database.hostname": "auth_db",
      "database.port": "5432",
      "database.user": "osama",
      "database.password": "123456",
      "database.dbname": "auth_db",
      "topic.prefix": "auth",
      "table.include.list": "public.outbox_events",
      "plugin.name": "pgoutput",
      "transforms": "outbox",
      "transforms.outbox.type": "io.debezium.transforms.outbox.EventRouter",
      "transforms.outbox.table.field.event.id": "id",
      "transforms.outbox.table.field.event.key": "aggregate_id",
      "transforms.outbox.table.field.event.payload": "payload",
      "transforms.outbox.route.by.field": "aggregate_type",
      "transforms.outbox.route.topic.replacement": "user-events",
      "transforms.outbox.table.fields.additional.placement": "event_type:header:eventType",
      "key.converter": "org.apache.kafka.connect.storage.StringConverter",
      "value.converter": "org.apache.kafka.connect.storage.StringConverter",
      "tombstones.on.delete": "false"
    }
  }'

echo ""
echo "Done. Verifying connector status:"
sleep 3
curl -sf "$CONNECT_URL/connectors/outbox-connector/status" | python3 -m json.tool 2>/dev/null \
  || curl -sf "$CONNECT_URL/connectors/outbox-connector/status"
echo ""