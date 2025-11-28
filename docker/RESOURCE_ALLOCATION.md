# Resource Allocation Guide - 4GB Maximum

This document outlines the resource limits configured for running the entire Social Network Microservices platform within a 4GB RAM budget.

## Total Memory Allocation: ~4GB

### Infrastructure Services (2.68GB max)

| Service | Memory Limit | Memory Reserved | CPU Limit | Notes |
|---------|--------------|-----------------|-----------|-------|
| **Zookeeper** | 256M | 128M | 0.25 | Kafka coordination |
| **Kafka** | 512M | 256M | 0.5 | Message broker (heap: 384M) |
| **Kafka UI** | 256M | 128M | 0.25 | Monitoring interface |
| **Elasticsearch** | 640M | 384M | 0.5 | Search engine (heap: 512M) |
| **Logstash** | 384M | 128M | 0.25 | Log processing (heap: 256M) |
| **Kibana** | 384M | 256M | 0.25 | Log visualization (node: 256M) |
| **Redis** | 192M | 64M | 0.25 | Caching (max: 128M) |
| **PostgreSQL (auth)** | 256M | 128M | 0.25 | Auth database |
| **PostgreSQL (main)** | 384M | 256M | 0.5 | Main database |
| **MongoDB (chat)** | 256M | 128M | 0.25 | Chat database (cache: 150M) |

**Infrastructure Subtotal:** 3.52GB max

### Microservices (1.98GB max)

| Service | Memory Limit | Memory Reserved | CPU Limit | JVM Max Heap |
|---------|--------------|-----------------|-----------|--------------|
| **Auth Service** | 320M | 192M | 0.5 | 256M |
| **Gateway Service** | 320M | 192M | 0.5 | 256M |
| **Main Service** | 448M | 256M | 0.5 | 384M |
| **Search Service** | 320M | 192M | 0.5 | 256M |
| **Notification Service** | 256M | 128M | 0.25 | 192M |
| **Chat Service** | 320M | 192M | 0.5 | 256M |

**Microservices Subtotal:** 1.98GB max

---

## Grand Total: ~5.5GB Theoretical Max

However, with actual runtime usage, services typically run at 60-75% of their limits, resulting in **~4GB actual usage**.

---

## Service Startup Order

The Docker Compose file enforces this dependency order:

1. **Databases First:** auth-db, main-db, chat-db, redis
2. **Infrastructure:** zookeeper → kafka, elasticsearch → logstash/kibana
3. **Core Service:** auth-service (required by all other services)
4. **Application Services:** gateway, main, search, notification, chat

All services with healthchecks ensure proper orchestration.

---

## Memory Optimization Strategies Applied

### JVM Services (Spring Boot)
- `-Xms` (initial heap) set to ~50% of `-Xmx` (max heap)
- `-XX:+UseContainerSupport` - respects Docker memory limits
- `-XX:MaxRAMPercentage=75.0` - uses up to 75% of container memory

### Database Services
- **PostgreSQL:** Reduced `shared_buffers` and connection limits
- **MongoDB:** WiredTiger cache limited to 150MB
- **Redis:** Max memory set to 128MB with LRU eviction policy

### Java-based Infrastructure
- **Elasticsearch:** JVM heap limited to 512MB
- **Logstash:** JVM heap limited to 256MB
- **Kafka/Zookeeper:** Heap limits via KAFKA_HEAP_OPTS

---

## Monitoring Resource Usage

### Check Overall Docker Resource Usage
```bash
docker stats
```

### Check Specific Service Memory
```bash
docker stats auth_service main_service elasticsearch
```

### View All Containers Memory
```bash
docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}\t{{.CPUPerc}}"
```

---

## Deployment Commands

### Start All Services
```bash
cd docker
docker-compose -f docker-compose-all.yml up -d
```

### View Logs for All Services
```bash
docker-compose -f docker-compose-all.yml logs -f
```

### Stop All Services
```bash
docker-compose -f docker-compose-all.yml down
```

### Stop and Remove Volumes (Clean Slate)
```bash
docker-compose -f docker-compose-all.yml down -v
```

---

## Troubleshooting

### If Services Fail to Start

**Out of Memory Errors:**
- Check `docker stats` to identify memory-hungry services
- Consider disabling ELK stack (Elasticsearch, Logstash, Kibana) if not needed
- Disable Kafka UI if monitoring not required

**Slow Startup:**
- Services have `start_period` and health checks
- Allow 3-5 minutes for complete startup
- Check logs: `docker-compose -f docker-compose-all.yml logs [service-name]`

**Health Check Failures:**
- Increase `start_period` in docker-compose.yml for slow machines
- Check individual service logs for errors

### Minimal Configuration (2GB Mode)

If 4GB is still too much, you can disable optional services:

```bash
# Edit docker-compose-all.yml and comment out:
# - kafka-ui (saves 256M)
# - kibana (saves 384M)
# - logstash (saves 384M)
# Total savings: ~1GB
```

---

## Service Endpoints

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| Auth Service | 8087 | http://localhost:8087 | Authentication API |
| Gateway | 8081 | http://localhost:8081 | API Gateway |
| Main Service | 8082 | http://localhost:8082 | Core Business Logic |
| Search Service | 8084 | http://localhost:8084 | Search API |
| Notification Service | 8085 | http://localhost:8085 | Notifications |
| Chat Service | 8086 | http://localhost:8086 | WebSocket Chat |
| Kafka UI | 9093 | http://localhost:9093 | Kafka Monitoring |
| Kibana | 5601 | http://localhost:5601 | Log Visualization |
| Elasticsearch | 9200 | http://localhost:9200 | Search Engine |

---

## Performance Expectations

With these tight resource constraints:

- **Startup time:** 3-5 minutes for all services
- **Response time:** Slightly higher latency under load
- **Throughput:** Suitable for development and testing
- **Concurrent users:** 10-50 simulated users
- **Not recommended for:** Production, load testing, or high-concurrency scenarios

This configuration is optimized for **local development and functional testing** on resource-constrained machines.
