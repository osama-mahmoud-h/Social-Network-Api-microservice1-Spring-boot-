#!/bin/bash

# Unified Docker Compose for Social Network Microservices
# This script starts all services using a single docker-compose file

echo "=========================================="
echo "Starting Social Network Microservices"
echo "=========================================="

# Stop and remove any existing containers
echo "Cleaning up existing containers..."
docker-compose -f docker-compose-all.yml down --remove-orphans

# Start all services with the unified docker-compose file
echo "Starting all services..."
docker-compose -f docker-compose-all.yml up -d --build

echo ""
echo "=========================================="
echo "All services are starting up!"
echo "=========================================="
echo ""
echo "Service URLs:"
echo "  - Eureka Dashboard:      http://localhost:8761"
echo "  - Gateway Service:       http://localhost:8081"
echo "  - Auth Service:          http://localhost:8087"
echo "  - Main Service:          http://localhost:8082"
echo "  - Search Service:        http://localhost:8084"
echo "  - Notification Service:  http://localhost:8085"
echo "  - Chat Service:          http://localhost:8086"
echo ""
echo "Infrastructure URLs:"
echo "  - Kafka UI:              http://localhost:9093"
echo "  - Elasticsearch:         http://localhost:9200"
echo "  - Kibana:                http://localhost:5601"
echo ""
echo "=========================================="
echo "To view logs: docker-compose -f docker-compose-all.yml logs -f [service-name]"
echo "To stop all:  docker-compose -f docker-compose-all.yml down"
echo "=========================================="
