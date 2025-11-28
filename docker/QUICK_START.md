# Quick Start Guide - 4GB Deployment

This guide will help you deploy the entire Social Network Microservices platform with resource limits optimized for 4GB RAM usage.

## Prerequisites

- Docker and Docker Compose installed
- At least 4GB of available RAM
- 10GB of free disk space
- Linux/macOS/WSL2 on Windows

## Option 1: Automated Deployment (Recommended)

### Deploy with Monitoring Script

```bash
cd docker
./deploy-with-monitoring.sh
```

This script will:
1. Check prerequisites
2. Create necessary Docker networks
3. Show resource allocation summary
4. Build and start all services
5. Monitor service health
6. Display service endpoints
7. Optionally start continuous resource monitoring

### Other Script Commands

```bash
# Monitor resource usage continuously
./deploy-with-monitoring.sh monitor

# Show current resource stats
./deploy-with-monitoring.sh stats

# Stop all services
./deploy-with-monitoring.sh stop

# Clean up (removes volumes and data)
./deploy-with-monitoring.sh clean
```

---

## Option 2: Manual Deployment

### Step 1: Create Docker Network

```bash
docker network create social_service_dev_network
```

### Step 2: Start All Services

```bash
cd docker
docker-compose -f docker-compose-all.yml up -d --build
```

**Note:** First run will take 5-10 minutes to build images.

### Step 3: Monitor Startup

```bash
# Watch all logs
docker-compose -f docker-compose-all.yml logs -f

# Watch specific service
docker-compose -f docker-compose-all.yml logs -f auth-service

# Check container status
docker ps
```

### Step 4: Monitor Resources

```bash
# Continuous monitoring
docker stats

# One-time check
docker stats --no-stream
```

---

## Service Startup Order & Wait Times

Services start in dependency order:

1. **Infrastructure (1-2 min)**
   - Databases: PostgreSQL (auth-db, main-db), MongoDB, Redis
   - Message Queue: Zookeeper → Kafka
   - Search: Elasticsearch → Logstash, Kibana

2. **Core Service (1-2 min)**
   - Auth Service (must start first)

3. **Application Services (1-2 min)**
   - Gateway, Main, Search, Notification, Chat

**Total startup time: 3-5 minutes**

---

## Verify Deployment

### Check Service Health

```bash
# Check all containers are running
docker ps

# Check specific service health
docker inspect auth_service | grep -A 5 "Health"

# Check logs for errors
docker-compose -f docker-compose-all.yml logs | grep -i error
```

### Test Service Endpoints

```bash
# Test Auth Service
curl http://localhost:8087/actuator/health

# Test Main Service
curl http://localhost:8082/actuator/health

# Test Elasticsearch
curl http://localhost:9200/_cluster/health

# Test Kafka UI (open in browser)
# http://localhost:9093

# Test Kibana (open in browser)
# http://localhost:5601
```

---

## Service Endpoints Reference

### Application Services

| Service | Port | Health Check | Swagger UI |
|---------|------|--------------|------------|
| Auth Service | 8087 | http://localhost:8087/actuator/health | http://localhost:8087/swagger-ui.html |
| Gateway Service | 8081 | - | - |
| Main Service | 8082 | - | http://localhost:8082/swagger-ui.html |
| Search Service | 8084 | - | - |
| Notification Service | 8085 | - | - |
| Chat Service | 8086 | - | - |

### Infrastructure

| Service | Port | Access |
|---------|------|--------|
| Kafka UI | 9093 | http://localhost:9093 |
| Kibana | 5601 | http://localhost:5601 |
| Elasticsearch | 9200 | http://localhost:9200 |

### Databases

| Database | Port | Connection String |
|----------|------|-------------------|
| PostgreSQL (auth) | 5433 | jdbc:postgresql://localhost:5433/auth_db |
| PostgreSQL (main) | 5434 | jdbc:postgresql://localhost:5434/social_db |
| MongoDB (chat) | 27018 | mongodb://localhost:27018/chat-service |
| Redis | 6379 | redis://localhost:6379 |

**Credentials:** username=`osama`, password=`123456`

---

## Image Management

### Avoid `<none>:<none>` Images

All services now have explicit image tags (e.g., `social-network/auth-service:latest`). This prevents dangling images during rebuilds.

### Clean Up Dangling Images

```bash
# Interactive cleanup tool
./cleanup-images.sh

# Or use specific commands
./cleanup-images.sh clean        # Remove dangling images
./cleanup-images.sh prune        # Remove all unused resources
./cleanup-images.sh rebuild      # Rebuild all with proper tags
```

### Manual Cleanup

```bash
# Remove dangling images
docker image prune -f

# Remove all unused images
docker image prune -a -f

# Remove everything (containers, networks, images, cache)
docker system prune -a --volumes -f
```

---

## Common Operations

### View Logs

```bash
# All services
docker-compose -f docker-compose-all.yml logs -f

# Specific service
docker-compose -f docker-compose-all.yml logs -f main-service

# Last 100 lines
docker-compose -f docker-compose-all.yml logs --tail=100

# Follow specific services
docker-compose -f docker-compose-all.yml logs -f auth-service main-service
```

### Restart Services

```bash
# Restart all
docker-compose -f docker-compose-all.yml restart

# Restart specific service
docker-compose -f docker-compose-all.yml restart main-service

# Rebuild and restart
docker-compose -f docker-compose-all.yml up -d --build main-service
```

### Stop Services

```bash
# Stop all (keeps data)
docker-compose -f docker-compose-all.yml stop

# Stop and remove containers (keeps data)
docker-compose -f docker-compose-all.yml down

# Stop, remove containers and volumes (deletes data)
docker-compose -f docker-compose-all.yml down -v
```

---

## Troubleshooting

### Service Won't Start

**Check logs:**
```bash
docker-compose -f docker-compose-all.yml logs [service-name]
```

**Check container status:**
```bash
docker ps -a | grep [service-name]
```

**Restart service:**
```bash
docker-compose -f docker-compose-all.yml restart [service-name]
```

### Out of Memory Errors

**Check memory usage:**
```bash
docker stats --no-stream
```

**Disable optional services** (edit docker-compose-all.yml and comment out):
- `kafka-ui` (saves 256M)
- `kibana` (saves 384M)
- `logstash` (saves 384M)

Then restart:
```bash
docker-compose -f docker-compose-all.yml up -d
```

### Port Already in Use

**Find process using port:**
```bash
sudo lsof -i :8087
```

**Kill process or change port** in docker-compose-all.yml:
```yaml
ports:
  - "8088:8087"  # Changed from 8087:8087
```

### Database Connection Errors

**Verify database is running:**
```bash
docker ps | grep _db
```

**Check database logs:**
```bash
docker logs auth_db
docker logs main_db
docker logs chat-service-db
```

**Recreate database:**
```bash
docker-compose -f docker-compose-all.yml stop main-db
docker-compose -f docker-compose-all.yml rm -f main-db
docker volume rm docker_main-db-data
docker-compose -f docker-compose-all.yml up -d main-db
```

### Slow Performance

This is expected with 4GB limit. To improve:

1. **Disable unnecessary services**
2. **Increase swap space:**
   ```bash
   # Check current swap
   free -h

   # Add swap if needed (8GB example)
   sudo fallocate -l 8G /swapfile
   sudo chmod 600 /swapfile
   sudo mkswap /swapfile
   sudo swapon /swapfile
   ```

3. **Close other applications** to free up RAM

---

## Resource Monitoring Tips

### Continuous Monitoring Dashboard

```bash
watch -n 2 'docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}\t{{.CPUPerc}}"'
```

### Memory by Service

```bash
docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}" | sort -k 2 -h
```

### Check Java Heap Usage (in running container)

```bash
# For Spring Boot services
docker exec -it main_service jstat -gc 1

# Or use JVisualVM/JConsole from your host (requires JMX)
```

---

## Development Workflow

### Making Code Changes

1. **Make changes** to your service code
2. **Rebuild specific service:**
   ```bash
   cd docker
   docker-compose -f docker-compose-all.yml build main-service
   docker-compose -f docker-compose-all.yml up -d main-service
   ```

3. **Watch logs:**
   ```bash
   docker-compose -f docker-compose-all.yml logs -f main-service
   ```

### Testing the Platform

1. **Register a user:** POST to `http://localhost:8087/api/auth/signup`
2. **Login:** POST to `http://localhost:8087/api/auth/login`
3. **Create post:** POST to `http://localhost:8082/api/posts`
4. **Search:** POST to `http://localhost:8084/api/search/multi-index`
5. **Chat:** Connect WebSocket to `ws://localhost:8086/ws`

---

## Next Steps

- Review [RESOURCE_ALLOCATION.md](./RESOURCE_ALLOCATION.md) for detailed resource breakdown
- Check service Swagger UIs for API documentation
- Monitor Kafka topics via Kafka UI (http://localhost:9093)
- View logs in Kibana (http://localhost:5601)

---

## Support

For issues or questions:
- Check service logs first
- Review CLAUDE.md for architecture details
- Check GitHub issues: https://github.com/anthropics/claude-code/issues
