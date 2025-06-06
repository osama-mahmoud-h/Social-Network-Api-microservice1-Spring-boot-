#!/bin/bash

# Create shared network via Docker for all services ✅
echo "Ensuring Docker network exists..."
docker network inspect social_service_dev_network >/dev/null 2>&1 || \
docker network create social_service_dev_network

# Start the services in detached mode
# 1. run ELK stack
docker-compose -f ./elk-stack-docker/docker-compose-elk.yml up -d --force-recreate

# 2. run Kafka
docker-compose -f docker-compose-kafka.yml up -d --force-recreate

# 3. run discovery service (Eureka) ✅
docker-compose -f ../services/discovery-service/docker/dev/docker-compose.yml up -d --force-recreate

# 4. run gateway-service ✅
docker-compose -f ../services/gateway-service/docker/dev/docker-compose.yml up -d --force-recreate

# 5. run main-service
docker-compose -f ../services/main-service/docker/dev/docker-compose.yml up -d --force-recreate

# 6. run search-service
docker-compose -f ../services/search-service/docker/dev/docker-compose.yml up -d --force-recreate

# 7. run notification-service
docker-compose -f ../services/notification-service/docker/dev/docker-compose.yml up -d --force-recreate

# 8. run chat-service
docker-compose -f ../services/chat-service/docker/dev/docker-compose.yml up -d --force-recreate
