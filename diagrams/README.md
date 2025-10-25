# Architecture Diagrams

This directory contains PlantUML diagrams for the Social Network Microservices architecture.

## Diagrams Overview

### 1. Microservices Architecture (`microservices-architecture.puml`)
High-level overview of the entire microservices ecosystem showing:
- All 7 microservices (Gateway, Auth, Main, Chat, Notification, Search, Discovery)
- Databases (PostgreSQL, MongoDB, Elasticsearch, Redis)
- Kafka message broker
- ELK stack for logging
- Service communication patterns

**Key Features:**
- Client to Gateway routing
- Service discovery with Eureka
- Inter-service communication (Feign clients)
- Event-driven architecture with Kafka
- Centralized logging

### 2. Kafka Event Flow (`kafka-event-flow.puml`)
Detailed sequence diagram showing event-driven communication:
- Post creation events (fan-out to friends)
- Comment creation events (notify post author)
- Friendship events (notifications)
- Kafka topic structure

**Topics:**
- `post-events` - Post CRUD operations
- `comment-events` - Comment CRUD operations
- `notification-events` - Friendship and reaction events

### 3. Authentication Flow (`authentication-flow.puml`)
Complete authentication and authorization flow:
- User login with JWT generation
- Token validation at Gateway level
- Token validation at Service level
- Feign client authentication
- Single device logout
- All devices logout

**Key Components:**
- JWT-based stateless authentication
- Token revocation tracking in database
- Multi-layer security (Gateway + Service filters)

### 4. Deployment Diagram (`deployment-diagram.puml`)
Docker-based deployment architecture showing:
- Docker containers for each service
- Network topology
- Port configurations
- Database deployments
- Kafka cluster setup
- ELK stack integration

### 5. WebSocket Chat Flow (`websocket-chat-flow.puml`)
Real-time chat communication patterns:
- WebSocket connection establishment
- User join/leave events
- Message sending and delivery
- Typing indicators
- Online user tracking with Redis
- Offline message handling (TODO)

**Event Types:**
- JOIN, SEND, TYPING, LEAVE (Client → Server)
- JOIN_ACK, MESSAGE, USER_JOINED, USER_LEFT (Server → Client)

### 6. System Components (`system-components.puml`)
Comprehensive component-level architecture:
- Frontend layer (Web, Mobile, Admin)
- Edge layer (Gateway, Load Balancer)
- Business services breakdown
- Data access layer
- Message broker topology
- Monitoring and logging components

## Viewing the Diagrams

### Option 1: PlantUML Online Server
1. Go to [PlantUML Web Server](http://www.plantuml.com/plantuml/uml/)
2. Copy the content of any `.puml` file
3. Paste and view the rendered diagram

### Option 2: VS Code Extension
1. Install "PlantUML" extension in VS Code
2. Open any `.puml` file
3. Press `Alt+D` to preview

### Option 3: Generate PNG/SVG
```bash
# Install PlantUML
sudo apt-get install plantuml

# Generate PNG images
plantuml diagrams/*.puml

# Generate SVG images
plantuml -tsvg diagrams/*.puml
```

### Option 4: IntelliJ IDEA Plugin
1. Install "PlantUML integration" plugin
2. Open `.puml` files to see live preview

## Integration with README

These diagrams are referenced in the main project README.md to provide visual documentation of the architecture.

## Updating Diagrams

When making architectural changes:
1. Update the relevant `.puml` file(s)
2. Regenerate images if using static images
3. Commit both `.puml` source and generated images
4. Update main README if new diagrams are added

## Diagram Formats

All diagrams use PlantUML format which offers:
- ✅ Version control friendly (plain text)
- ✅ Easy to update and maintain
- ✅ Automatic layout
- ✅ Consistent styling
- ✅ Can generate multiple output formats (PNG, SVG, PDF)

## Color Coding

- 🟠 **Orange**: Gateway and Edge services
- 🟣 **Purple**: Service Discovery (Eureka)
- 🟢 **Green**: Business/Core services
- 🔵 **Blue**: Client applications
- 🟡 **Yellow**: Message brokers (Kafka)
- 🟤 **Wheat**: Databases
- 🌸 **Pink**: Monitoring and Logging

## Notes

- Diagrams reflect the current architecture as of the last update
- Some features marked as TODO are planned but not yet implemented
- Security improvements (especially WebSocket auth) are documented as pending
