#!/bin/bash

# ELK Stack Startup Script
# This script starts the ELK stack and verifies all components are running

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "========================================="
echo "Starting ELK Stack"
echo "========================================="
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo " Error: Docker is not running. Please start Docker and try again."
    exit 1
fi

# Create network if it doesn't exist
echo " Checking Docker network..."
if ! docker network inspect social_service_dev_network > /dev/null 2>&1; then
    echo "Creating social_service_dev_network..."
    docker network create social_service_dev_network
    echo " Network created"
else
    echo " Network already exists"
fi
echo ""

# Start ELK stack
echo " Starting ELK containers..."
docker-compose -f docker-compose-elk.yml up -d

echo ""
echo " Waiting for services to be ready..."
sleep 5

# Check container status
echo ""
echo " Container Status:"
docker-compose -f docker-compose-elk.yml ps

# Wait for Elasticsearch to be ready
echo ""
echo " Waiting for Elasticsearch to be ready..."
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:9200/_cluster/health > /dev/null 2>&1; then
        echo " Elasticsearch is ready!"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "   Attempt $RETRY_COUNT/$MAX_RETRIES..."
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo " Elasticsearch failed to start within expected time"
    exit 1
fi

# Check Elasticsearch health
echo ""
echo " Elasticsearch Health:"
curl -s http://localhost:9200/_cluster/health?pretty | grep -E "status|number_of_nodes"

# Wait for Logstash to be ready
echo ""
echo " Checking Logstash status..."
sleep 3
if docker logs logstash 2>&1 | grep -q "Successfully started Logstash"; then
    echo "✅ Logstash is ready!"
else
    echo "  Logstash is starting... (may take a minute)"
fi

# Wait for Kibana to be ready
echo ""
echo "⏳ Waiting for Kibana to be ready..."
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:5601/api/status > /dev/null 2>&1; then
        echo "✅ Kibana is ready!"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "   Attempt $RETRY_COUNT/$MAX_RETRIES..."
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "  Kibana is still starting... You may need to wait a bit longer"
fi

echo ""
echo "========================================="
echo " ELK Stack Started Successfully!"
echo "========================================="
echo ""
echo " Access URLs:"
echo "   Elasticsearch: http://localhost:9200"
echo "   Kibana:        http://localhost:5601"
echo "   Logstash:      localhost:5000 (TCP)"
echo ""
echo " Next Steps:"
echo "   1. Open Kibana: http://localhost:5601"
echo "   2. Start your microservices"
echo "   3. Create index pattern in Kibana: microservices-*"
echo "   4. View logs in the Discover tab"
echo ""
echo " For detailed setup instructions, see:"
echo "   ./ELK-SETUP-GUIDE.md"
echo ""
echo " To stop ELK stack:"
echo "   docker-compose -f docker-compose-elk.yml down"
echo ""
